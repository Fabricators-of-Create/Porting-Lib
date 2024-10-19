package io.github.fabricators_of_create.porting_lib.models;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.math.Transformation;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import io.github.fabricators_of_create.porting_lib.models.geometry.IGeometryBakingContext;
import io.github.fabricators_of_create.porting_lib.models.geometry.IGeometryLoader;
import io.github.fabricators_of_create.porting_lib.models.geometry.IUnbakedGeometry;
import io.github.fabricators_of_create.porting_lib.models.geometry.SimpleModelState;
import io.github.fabricators_of_create.porting_lib.models.geometry.StandaloneGeometryBakingContext;
import io.github.fabricators_of_create.porting_lib.util.FluidUtil;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * A dynamic fluid container model, capable of re-texturing itself at runtime to match the contained fluid.
 * <p>
 * Composed of a base layer, a fluid layer (applied with a mask) and a cover layer (optionally applied with a mask).
 * The entire model may optionally be flipped if the fluid is gaseous, and the fluid layer may glow if light-emitting.
 * <p>
 * Fluid tinting requires registering a separate {@link ItemColor}. An implementation is provided in {@link Colors}.
 *
 * @see Colors
 */
public class DynamicFluidContainerModel implements IUnbakedGeometry<DynamicFluidContainerModel> {
	// Depth offsets to prevent Z-fighting
	private static final Transformation FLUID_TRANSFORM = new Transformation(new Vector3f(), new Quaternionf(), new Vector3f(1, 1, 1.002f), new Quaternionf());
	private static final Transformation COVER_TRANSFORM = new Transformation(new Vector3f(), new Quaternionf(), new Vector3f(1, 1, 1.004f), new Quaternionf());

	private final Fluid fluid;
	private final boolean flipGas;
	private final boolean coverIsMask;
	private final boolean applyFluidLuminosity;

	private DynamicFluidContainerModel(Fluid fluid, boolean flipGas, boolean coverIsMask, boolean applyFluidLuminosity) {
		this.fluid = fluid;
		this.flipGas = flipGas;
		this.coverIsMask = coverIsMask;
		this.applyFluidLuminosity = applyFluidLuminosity;
	}

	public static RenderTypeGroup getLayerRenderTypes(boolean unlit) {
		// Must be solid or else water texture will break the rendering
		return new RenderTypeGroup(RenderType.translucent(), RenderType.solid()); // unlit ? NeoForgeRenderTypes.ITEM_UNSORTED_UNLIT_TRANSLUCENT.get() : NeoForgeRenderTypes.ITEM_UNSORTED_TRANSLUCENT.get());
	}

	/**
	 * Returns a new ModelDynBucket representing the given fluid, but with the same
	 * other properties (flipGas, tint, coverIsMask).
	 */
	public DynamicFluidContainerModel withFluid(Fluid newFluid) {
		return new DynamicFluidContainerModel(newFluid, flipGas, coverIsMask, applyFluidLuminosity);
	}

	@Nullable
	@Override
	public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides) {
		Material particleLocation = context.hasMaterial("particle") ? context.getMaterial("particle") : null;
		Material baseLocation = context.hasMaterial("base") ? context.getMaterial("base") : null;
		Material fluidMaskLocation = context.hasMaterial("fluid") ? context.getMaterial("fluid") : null;
		Material coverLocation = context.hasMaterial("cover") ? context.getMaterial("cover") : null;

		TextureAtlasSprite baseSprite = baseLocation != null ? spriteGetter.apply(baseLocation) : null;
		TextureAtlasSprite templateSprite = fluidMaskLocation != null ? spriteGetter.apply(fluidMaskLocation) : null;
		TextureAtlasSprite coverSprite = (coverLocation != null && (!coverIsMask || baseLocation != null)) ? spriteGetter.apply(coverLocation) : null;

		TextureAtlasSprite particleSprite = particleLocation != null ? spriteGetter.apply(particleLocation) : null;

		// We need to disable GUI 3D and block lighting for this to render properly
		var itemContext = StandaloneGeometryBakingContext.builder(context).withGui3d(false).withUseBlockLight(false).build(PortingLib.id("dynamic_fluid_container"));
		var overrideHandler = new ContainedFluidOverrideHandler(overrides, baker, itemContext, this);

		// It is necessary to use a LazyBakedModel here because fluid textures are not loaded yet on game start
		// and would lead to fluid containers without fluids.
		return new LazyBakedModel(itemContext, baseSprite, templateSprite, coverSprite, particleSprite, modelState, overrideHandler);
	}

	public final class LazyBakedModel implements BakedModel {
		private final IGeometryBakingContext itemContext;
		private final TextureAtlasSprite baseSprite;
		private final TextureAtlasSprite templateSprite;
		private final TextureAtlasSprite coverSprite;
		private final TextureAtlasSprite particleSprite;
		private final ModelState modelState;
		private final ItemOverrides overrides;

		private BakedModel compositeModel;

		private LazyBakedModel(IGeometryBakingContext itemContext, TextureAtlasSprite baseSprite, TextureAtlasSprite templateSprite, TextureAtlasSprite coverSprite, TextureAtlasSprite particleSprite, ModelState modelState, ItemOverrides overrides) {
			this.itemContext = itemContext;
			this.baseSprite = baseSprite;
			this.templateSprite = templateSprite;
			this.coverSprite = coverSprite;
			this.particleSprite = particleSprite;
			this.modelState = modelState;
			this.overrides = overrides;
		}

		private BakedModel wrapped() {
			if (compositeModel == null) {
				compositeModel = initializeWrappedModel();
			}

			return compositeModel;
		}

		private BakedModel initializeWrappedModel() {
			ModelState modelState = this.modelState;
			// If the fluid is lighter than air, rotate 180deg to turn it upside down
			if (flipGas && fluid != Fluids.EMPTY && fluid.getFluidType().isLighterThanAir()) {
				modelState = new SimpleModelState(
						this.modelState.getRotation().compose(
								new Transformation(null, new Quaternionf(0, 0, 1, 0), null, null)));
			}

			// Initializer must be in the if statement to make it usable in lambdas
			TextureAtlasSprite fluidSprite;
			if (fluid != Fluids.EMPTY) {
				fluidSprite = FluidVariantRendering.getSprite(FluidVariant.of(fluid));
			} else {
				fluidSprite = null;
			}

			var modelBuilder = CompositeModel.Baked.builder(itemContext, particleSprite, overrides, itemContext.getTransforms());

			TextureAtlasSprite particleSprite = this.particleSprite;
			if (particleSprite == null) particleSprite = fluidSprite;
			if (particleSprite == null) particleSprite = baseSprite;
			if (particleSprite == null && !coverIsMask) particleSprite = coverSprite;

			var normalRenderTypes = getLayerRenderTypes(false);
			if (baseSprite != null) {
				// Base texture
				var unbaked = UnbakedGeometryHelper.createUnbakedItemElements(0, baseSprite);
				var quads = UnbakedGeometryHelper.bakeElements(unbaked, $ -> baseSprite, modelState);
				modelBuilder.addQuads(normalRenderTypes, quads);
			}

			if (templateSprite != null && fluidSprite != null) {
				// Fluid layer
				var transformedState = new SimpleModelState(modelState.getRotation().compose(FLUID_TRANSFORM), modelState.isUvLocked());
				var unbaked = UnbakedGeometryHelper.createUnbakedItemMaskElements(1, templateSprite); // Use template as mask
				var quads = UnbakedGeometryHelper.bakeElements(unbaked, $ -> fluidSprite, transformedState); // Bake with fluid texture

				var emissive = applyFluidLuminosity && fluid.getFluidType().getLightLevel() > 0;
				var renderTypes = getLayerRenderTypes(emissive);

				var material = RendererAccess.INSTANCE.getRenderer().materialFinder().blendMode(BlendMode.fromRenderLayer(renderTypes.entity())).emissive(emissive).find();
				var builder = RendererAccess.INSTANCE.getRenderer().meshBuilder();
				quads.forEach(quad -> builder.getEmitter().fromVanilla(quad, material, null).emit());
				modelBuilder.addLayer(new MeshBakedModel(builder.build(), itemContext.useAmbientOcclusion(), itemContext.useBlockLight(), itemContext.isGui3d(), particleSprite, itemContext.getTransforms(), overrides));
			}

			if (coverSprite != null) {
				var sprite = coverIsMask ? baseSprite : coverSprite;
				if (sprite != null) {
					// Cover/overlay
					var transformedState = new SimpleModelState(modelState.getRotation().compose(COVER_TRANSFORM), modelState.isUvLocked());
					var unbaked = UnbakedGeometryHelper.createUnbakedItemMaskElements(2, coverSprite); // Use cover as mask
					var quads = UnbakedGeometryHelper.bakeElements(unbaked, $ -> sprite, transformedState); // Bake with selected texture
					modelBuilder.addQuads(normalRenderTypes, quads);
				}
			}

			modelBuilder.setParticle(particleSprite);

			return modelBuilder.build();
		}

		@Override
		public boolean isVanillaAdapter() {
			// Need to use fabrics rendering api because with the getQuads function water textures still break the rendering
			return false;
		}

		@Override
		public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
			wrapped().emitBlockQuads(blockView, state, pos, randomSupplier, context);
		}

		@Override
		public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
			wrapped().emitItemQuads(stack, randomSupplier, context);
		}

		@Override
		public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource random) {
			return wrapped().getQuads(state, side, random);
		}

		@Override
		public boolean useAmbientOcclusion() {
			return wrapped().useAmbientOcclusion();
		}

		@Override
		public boolean isGui3d() {
			return wrapped().isGui3d();
		}

		@Override
		public boolean isCustomRenderer() {
			return false;
		}

		@Override
		public TextureAtlasSprite getParticleIcon() {
			return wrapped().getParticleIcon();
		}

		@Override
		public boolean usesBlockLight() {
			return wrapped().usesBlockLight();
		}

		@Override
		public ItemTransforms getTransforms() {
			return wrapped().getTransforms();
		}

		@Override
		public ItemOverrides getOverrides() {
			return wrapped().getOverrides();
		}
	}

	public static final class Loader implements IGeometryLoader<DynamicFluidContainerModel> {
		public static final Loader INSTANCE = new Loader();

		private Loader() {}

		@Override
		public DynamicFluidContainerModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) {
			if (!jsonObject.has("fluid"))
				throw new RuntimeException("Bucket model requires 'fluid' value.");

			ResourceLocation fluidName = ResourceLocation.parse(jsonObject.get("fluid").getAsString());

			Fluid fluid = BuiltInRegistries.FLUID.get(fluidName);

			boolean flip = GsonHelper.getAsBoolean(jsonObject, "flip_gas", false);
			boolean coverIsMask = GsonHelper.getAsBoolean(jsonObject, "cover_is_mask", true);
			boolean applyFluidLuminosity = GsonHelper.getAsBoolean(jsonObject, "apply_fluid_luminosity", true);

			// create new model with correct liquid
			return new DynamicFluidContainerModel(fluid, flip, coverIsMask, applyFluidLuminosity);
		}
	}

	private static final class ContainedFluidOverrideHandler extends ItemOverrides {
		private final Map<String, BakedModel> cache = Maps.newHashMap(); // contains all the baked models since they'll never change
		private final ItemOverrides nested;
		private final ModelBaker baker;
		private final IGeometryBakingContext owner;
		private final DynamicFluidContainerModel parent;

		private ContainedFluidOverrideHandler(ItemOverrides nested, ModelBaker baker, IGeometryBakingContext owner, DynamicFluidContainerModel parent) {
			this.nested = nested;
			this.baker = baker;
			this.owner = owner;
			this.parent = parent;
		}

		@Override
		public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
			BakedModel overridden = nested.resolve(originalModel, stack, level, entity, seed);
			if (overridden != originalModel) return overridden;
			return FluidUtil.getFluidContained(stack)
					.map(fluidStack -> {
						Fluid fluid = fluidStack.getFluid();
						String name = BuiltInRegistries.FLUID.getKey(fluid).toString();

						if (!cache.containsKey(name)) {
							DynamicFluidContainerModel unbaked = this.parent.withFluid(fluid);
							BakedModel bakedModel = unbaked.bake(owner, baker, Material::sprite, BlockModelRotation.X0_Y0, this);
							cache.put(name, bakedModel);
							return bakedModel;
						}

						return cache.get(name);
					})
					// not a fluid item apparently
					.orElse(originalModel); // empty bucket
		}
	}

	public static class Colors implements ItemColor {
		@Override
		public int getColor(ItemStack stack, int tintIndex) {
			if (tintIndex != 1) return 0xFFFFFFFF;
			return FluidUtil.getFluidContained(stack)
					.map(fluidStack -> FluidVariantRendering.getColor(fluidStack.getVariant()))
					.orElse(0xFFFFFFFF);
		}
	}
}
