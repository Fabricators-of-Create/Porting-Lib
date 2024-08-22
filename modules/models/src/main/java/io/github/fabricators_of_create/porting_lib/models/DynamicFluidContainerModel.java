package io.github.fabricators_of_create.porting_lib.models;

import java.util.Map;
import java.util.function.Function;

import io.github.fabricators_of_create.porting_lib.models.geometry.SimpleModelState;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.math.Transformation;

import io.github.fabricators_of_create.porting_lib.models.geometry.IGeometryLoader;
import io.github.fabricators_of_create.porting_lib.models.geometry.IUnbakedGeometry;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRendering;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

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
	private static final Logger LOGGER = LogManager.getLogger();

	// Depth offsets to prevent Z-fighting
	private static final Transformation FLUID_TRANSFORM = new Transformation(new Vector3f(), new Quaternionf(), new Vector3f(1, 1, 1.002f), new Quaternionf());
	private static final Transformation COVER_TRANSFORM = new Transformation(new Vector3f(), new Quaternionf(), new Vector3f(1, 1, 1.004f), new Quaternionf());

	private final Fluid fluid;
	private final boolean flipGas;
	private final boolean coverIsMask;
	private final boolean applyFluidLuminosity;
	private final boolean deprecatedLoader;
	private final Map<String, String> deprecationWarnings;

	public DynamicFluidContainerModel(Fluid fluid, boolean flipGas, boolean coverIsMask, boolean applyFluidLuminosity) {
		this(fluid, flipGas, coverIsMask, applyFluidLuminosity, false, Map.of());
	}

	private DynamicFluidContainerModel(Fluid fluid, boolean flipGas, boolean coverIsMask, boolean applyFluidLuminosity, boolean deprecatedLoader, Map<String, String> deprecationWarnings) {
		this.fluid = fluid;
		this.flipGas = flipGas;
		this.coverIsMask = coverIsMask;
		this.applyFluidLuminosity = applyFluidLuminosity;
		this.deprecatedLoader = deprecatedLoader;
		this.deprecationWarnings = deprecationWarnings;
	}

	/**
	 * Returns a new ModelDynBucket representing the given fluid, but with the same
	 * other properties (flipGas, tint, coverIsMask).
	 */
	public DynamicFluidContainerModel withFluid(Fluid newFluid) {
		return new DynamicFluidContainerModel(newFluid, flipGas, coverIsMask, applyFluidLuminosity, false, Map.of());
	}

	@Override
	public BakedModel bake(BlockModel context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation, boolean isGui3d) {
		if (deprecatedLoader)
			LOGGER.warn("Model \"" + modelLocation + "\" is using the deprecated loader \"forge:bucket\" instead of \"forge:fluid_container\". This loader will be removed in 1.20.");
		for (var entry : deprecationWarnings.entrySet())
			LOGGER.warn("Model \"" + modelLocation + "\" is using the deprecated \"" + entry.getKey() + "\" field in its fluid container model instead of \"" + entry.getValue() + "\". This field will be removed in 1.20.");

		Material particleLocation = context.hasTexture("particle") ? context.getMaterial("particle") : null;
		Material baseLocation = context.hasTexture("base") ? context.getMaterial("base") : null;
		Material fluidMaskLocation = context.hasTexture("fluid") ? context.getMaterial("fluid") : null;
		Material coverLocation = context.hasTexture("cover") ? context.getMaterial("cover") : null;

		TextureAtlasSprite baseSprite = baseLocation != null ? spriteGetter.apply(baseLocation) : null;
		TextureAtlasSprite fluidSprite = FluidVariantRendering.getSprite(FluidVariant.of(fluid));
		TextureAtlasSprite coverSprite = (coverLocation != null && (!coverIsMask || baseLocation != null)) ? spriteGetter.apply(coverLocation) : null;

		TextureAtlasSprite particleSprite = particleLocation != null ? spriteGetter.apply(particleLocation) : null;

		if (particleSprite == null) particleSprite = fluidSprite;
		if (particleSprite == null) particleSprite = baseSprite;
		if (particleSprite == null && !coverIsMask) particleSprite = coverSprite;

		// If the fluid is lighter than air, rotate 180deg to turn it upside down
		if (flipGas && fluid != Fluids.EMPTY && FluidVariantAttributes.isLighterThanAir(FluidVariant.of(fluid))) {
			modelState = new SimpleModelState(
					modelState.getRotation().compose(
							new Transformation(null, new Quaternionf(0, 0, 1, 0), null, null)));
		}

		// We need to disable GUI 3D and block lighting for this to render properly
		var modelBuilder = CompositeModel.Baked.builder(context.hasAmbientOcclusion(), false, context.getGuiLight().lightLikeBlock(), particleSprite, new ContainedFluidOverrideHandler(overrides, baker, context, this), context.getTransforms());

		if (baseLocation != null && baseSprite != null) {
			// Base texture
			var unbaked = UnbakedGeometryHelper.createUnbakedItemElements(0, baseSprite.contents());
			var quads = UnbakedGeometryHelper.bakeElements(unbaked, $ -> baseSprite, modelState, modelLocation);
			modelBuilder.addQuads(quads);
		}

		if (fluidMaskLocation != null && fluidSprite != null) {
			TextureAtlasSprite templateSprite = spriteGetter.apply(fluidMaskLocation);
			if (templateSprite != null) {
				// Fluid layer
				var transformedState = new SimpleModelState(modelState.getRotation().compose(FLUID_TRANSFORM), modelState.isUvLocked());
				var unbaked = UnbakedGeometryHelper.createUnbakedItemMaskElements(1, templateSprite.contents()); // Use template as mask
				var quads = UnbakedGeometryHelper.bakeElements(unbaked, $ -> fluidSprite, transformedState, modelLocation); // Bake with fluid texture

				modelBuilder.addQuads(quads);
			}
		}

		if (coverSprite != null) {
			var sprite = coverIsMask ? baseSprite : coverSprite;
			if (sprite != null) {
				// Cover/overlay
				var transformedState = new SimpleModelState(modelState.getRotation().compose(COVER_TRANSFORM), modelState.isUvLocked());
				var unbaked = UnbakedGeometryHelper.createUnbakedItemMaskElements(2, coverSprite.contents()); // Use cover as mask
				var quads = UnbakedGeometryHelper.bakeElements(unbaked, $ -> sprite, transformedState, modelLocation); // Bake with selected texture
				modelBuilder.addQuads(quads);
			}
		}

		modelBuilder.setParticle(particleSprite);

		return modelBuilder.build();
	}

	public static final class Loader implements IGeometryLoader<DynamicFluidContainerModel> {
		public static final Loader INSTANCE = new Loader(false);
		@Deprecated(forRemoval = true, since = "1.19")
		public static final Loader INSTANCE_DEPRECATED = new Loader(true);

		private final boolean deprecated;

		private Loader(boolean deprecated) {
			this.deprecated = deprecated;
		}

		@Override
		public DynamicFluidContainerModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) {
			if (!jsonObject.has("fluid"))
				throw new RuntimeException("Bucket model requires 'fluid' value.");

			ResourceLocation fluidName = ResourceLocation.parse(jsonObject.get("fluid").getAsString());

			Fluid fluid = BuiltInRegistries.FLUID.get(fluidName);

			boolean flip = GsonHelper.getAsBoolean(jsonObject, "flip_gas", false);
			boolean coverIsMask = GsonHelper.getAsBoolean(jsonObject, "cover_is_mask", true);
			boolean applyFluidLuminosity = GsonHelper.getAsBoolean(jsonObject, "apply_fluid_luminosity", true);

			// TODO: Deprecated names. To be removed in 1.20
			var deprecationWarningsBuilder = ImmutableMap.<String, String>builder();
			if (jsonObject.has("flipGas")) {
				flip = GsonHelper.getAsBoolean(jsonObject, "flipGas");
				deprecationWarningsBuilder.put("flipGas", "flip_gas");
			}
			if (jsonObject.has("coverIsMask")) {
				coverIsMask = GsonHelper.getAsBoolean(jsonObject, "coverIsMask");
				deprecationWarningsBuilder.put("coverIsMask", "cover_is_mask");
			}
			if (jsonObject.has("applyFluidLuminosity")) {
				applyFluidLuminosity = GsonHelper.getAsBoolean(jsonObject, "applyFluidLuminosity");
				deprecationWarningsBuilder.put("applyFluidLuminosity", "apply_fluid_luminosity");
			}

			// create new model with correct liquid
			return new DynamicFluidContainerModel(fluid, flip, coverIsMask, applyFluidLuminosity, deprecated, deprecationWarningsBuilder.build());
		}
	}

	private static final class ContainedFluidOverrideHandler extends ItemOverrides {
		private final Map<String, BakedModel> cache = Maps.newHashMap(); // contains all the baked models since they'll never change
		private final ItemOverrides nested;
		private final ModelBaker baker;
		private final BlockModel owner;
		private final DynamicFluidContainerModel parent;

		private ContainedFluidOverrideHandler(ItemOverrides nested, ModelBaker baker, BlockModel owner, DynamicFluidContainerModel parent) {
			this.nested = nested;
			this.baker = baker;
			this.owner = owner;
			this.parent = parent;
		}

		@Override
		public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
			BakedModel overridden = nested.resolve(originalModel, stack, level, entity, seed);
			if (overridden != originalModel) return overridden;
			return TransferUtil.getFluidContained(stack)
					.map(fluidStack -> {
						Fluid fluid = fluidStack.getFluid();
						String name = BuiltInRegistries.FLUID.getKey(fluid).toString();

						if (!cache.containsKey(name)) {
							DynamicFluidContainerModel unbaked = this.parent.withFluid(fluid);
							BakedModel bakedModel = unbaked.bake(owner, baker, Material::sprite, BlockModelRotation.X0_Y0, this, ResourceLocation.parse("neoforge:bucket_override"), false);
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
		public int getColor(@NotNull ItemStack stack, int tintIndex) {
			if (tintIndex != 1) return 0xFFFFFFFF;
			return TransferUtil.getFluidContained(stack)
					.map(fluidStack -> FluidVariantRendering.getColor(fluidStack.getVariant()))
					.orElse(0xFFFFFFFF);
		}
	}
}
