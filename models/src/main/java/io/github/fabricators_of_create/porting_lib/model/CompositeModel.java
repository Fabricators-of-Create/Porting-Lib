package io.github.fabricators_of_create.porting_lib.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import io.github.fabricators_of_create.porting_lib.client.RenderTypeGroup;
import io.github.fabricators_of_create.porting_lib.model.geometry.IGeometryBakingContext;
import io.github.fabricators_of_create.porting_lib.model.geometry.IGeometryLoader;
import io.github.fabricators_of_create.porting_lib.model.geometry.IUnbakedGeometry;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;

import io.github.fabricators_of_create.porting_lib.model.data.ModelData;
import io.github.fabricators_of_create.porting_lib.model.data.ModelProperty;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A model composed of several named children.
 * <p>
 * These respect component visibility as specified in {@link IGeometryBakingContext} and can additionally be provided
 * with an item-specific render ordering, for multi-pass arrangements.
 */
public class CompositeModel implements IUnbakedGeometry<CompositeModel> {
	private static final Logger LOGGER = LogManager.getLogger();

	private final ImmutableMap<String, BlockModel> children;
	private final ImmutableList<String> itemPasses;
	private final boolean logWarning;

	public CompositeModel(ImmutableMap<String, BlockModel> children, ImmutableList<String> itemPasses) {
		this(children, itemPasses, false);
	}

	private CompositeModel(ImmutableMap<String, BlockModel> children, ImmutableList<String> itemPasses, boolean logWarning) {
		this.children = children;
		this.itemPasses = itemPasses;
		this.logWarning = logWarning;
	}

	@Override
	public BakedModel bake(IGeometryBakingContext context, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
		if (logWarning)
			LOGGER.warn("Model \"" + modelLocation + "\" is using the deprecated \"parts\" field in its composite model instead of \"children\". This field will be removed in 1.20.");

		Material particleLocation = context.getMaterial("particle");
		TextureAtlasSprite particle = spriteGetter.apply(particleLocation);

		var rootTransform = context.getRootTransform();
		if (!rootTransform.isIdentity())
			modelState = new SimpleModelState(modelState.getRotation().compose(rootTransform), modelState.isUvLocked());

		var bakedPartsBuilder = ImmutableMap.<String, BakedModel>builder();
		for (var entry : children.entrySet()) {
			var name = entry.getKey();
			if (!context.isComponentVisible(name, true))
				continue;
			var model = entry.getValue();
			bakedPartsBuilder.put(name, model.bake(bakery, model, spriteGetter, modelState, modelLocation, true));
		}
		var bakedParts = bakedPartsBuilder.build();

		var itemPassesBuilder = ImmutableList.<BakedModel>builder();
		for (String name : this.itemPasses) {
			var model = bakedParts.get(name);
			if (model == null)
				throw new IllegalStateException("Specified \"" + name + "\" in \"item_render_order\", but that is not a child of this model.");
			itemPassesBuilder.add(model);
		}

		return new Baked(context.isGui3d(), context.useBlockLight(), context.useAmbientOcclusion(), particle, context.getTransforms(), overrides, bakedParts, itemPassesBuilder.build());
	}

	@Override
	public Collection<Material> getMaterials(IGeometryBakingContext context, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
		Set<Material> textures = new HashSet<>();
		if (context.hasMaterial("particle"))
			textures.add(context.getMaterial("particle"));
		for (BlockModel part : children.values())
			textures.addAll(part.getMaterials(modelGetter, missingTextureErrors));
		return textures;
	}

	@Override
	public Set<String> getConfigurableComponentNames() {
		return children.keySet();
	}

	public static class Baked implements BakedModel, FabricBakedModel {
		private final boolean isAmbientOcclusion;
		private final boolean isGui3d;
		private final boolean isSideLit;
		private final TextureAtlasSprite particle;
		private final ItemOverrides overrides;
		private final ItemTransforms transforms;
		private final ImmutableMap<String, BakedModel> children;
		private final ImmutableList<BakedModel> itemPasses;

		public Baked(boolean isGui3d, boolean isSideLit, boolean isAmbientOcclusion, TextureAtlasSprite particle, ItemTransforms transforms, ItemOverrides overrides, ImmutableMap<String, BakedModel> children, ImmutableList<BakedModel> itemPasses) {
			this.children = children;
			this.isAmbientOcclusion = isAmbientOcclusion;
			this.isGui3d = isGui3d;
			this.isSideLit = isSideLit;
			this.particle = particle;
			this.overrides = overrides;
			this.transforms = transforms;
			this.itemPasses = itemPasses;
		}

		@Override
		public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
			if (blockView instanceof RenderAttachedBlockView renderAttachedBlockView)
				for (Map.Entry<String, BakedModel> entry : children.entrySet())
					((FabricBakedModel) entry.getValue()).emitBlockQuads(new CustomDataBlockView(renderAttachedBlockView, CompositeModel.Data.resolve(ModelData.EMPTY, entry.getKey())), state, pos, randomSupplier, context);
		}

		@Override
		public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
			for (Map.Entry<String, BakedModel> entry : children.entrySet()) {
				((FabricBakedModel) entry.getValue()).emitItemQuads(stack, randomSupplier, context);
			}
		}

		@Override
		public boolean isVanillaAdapter() {
			return false;
		}

		@Override
		public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction face, RandomSource random) {
			return Collections.emptyList();
		}

		@Override
		public boolean useAmbientOcclusion() {
			return isAmbientOcclusion;
		}

		@Override
		public boolean isGui3d() {
			return isGui3d;
		}

		@Override
		public boolean usesBlockLight() {
			return isSideLit;
		}

		@Override
		public boolean isCustomRenderer() {
			return false;
		}

		@Override
		public TextureAtlasSprite getParticleIcon() {
			return particle;
		}

		@Override
		public ItemOverrides getOverrides() {
			return overrides;
		}

		@Override
		public ItemTransforms getTransforms() {
			return transforms;
		}

		@Nullable
		public BakedModel getPart(String name) {
			return children.get(name);
		}

		public static Builder builder(IGeometryBakingContext owner, TextureAtlasSprite particle, ItemOverrides overrides, ItemTransforms cameraTransforms) {
			return builder(owner.useAmbientOcclusion(), owner.isGui3d(), owner.useBlockLight(), particle, overrides, cameraTransforms);
		}

		public static Builder builder(boolean isAmbientOcclusion, boolean isGui3d, boolean isSideLit, TextureAtlasSprite particle, ItemOverrides overrides, ItemTransforms cameraTransforms) {
			return new Builder(isAmbientOcclusion, isGui3d, isSideLit, particle, overrides, cameraTransforms);
		}

		public static class Builder {
			private final boolean isAmbientOcclusion;
			private final boolean isGui3d;
			private final boolean isSideLit;
			private final List<BakedModel> children = new ArrayList<>();
			private final List<BakedQuad> quads = new ArrayList<>();
			private final ItemOverrides overrides;
			private final ItemTransforms transforms;
			private TextureAtlasSprite particle;
			private RenderTypeGroup lastRenderTypes = RenderTypeGroup.EMPTY;

			private Builder(boolean isAmbientOcclusion, boolean isGui3d, boolean isSideLit, TextureAtlasSprite particle, ItemOverrides overrides, ItemTransforms transforms) {
				this.isAmbientOcclusion = isAmbientOcclusion;
				this.isGui3d = isGui3d;
				this.isSideLit = isSideLit;
				this.particle = particle;
				this.overrides = overrides;
				this.transforms = transforms;
			}

			public void addLayer(BakedModel model) {
				flushQuads(null);
				children.add(model);
			}

			private void addLayer(RenderTypeGroup renderTypes, List<BakedQuad> quads) {
				var modelBuilder = IModelBuilder.of(isAmbientOcclusion, isSideLit, isGui3d, transforms, overrides, particle, renderTypes);
				quads.forEach(modelBuilder::addUnculledFace);
				children.add(modelBuilder.build());
			}

			private void flushQuads(RenderTypeGroup renderTypes) {
				if (!Objects.equals(renderTypes, lastRenderTypes)) {
					if (quads.size() > 0) {
						addLayer(lastRenderTypes, quads);
						quads.clear();
					}
					lastRenderTypes = renderTypes;
				}
			}

			public Builder setParticle(TextureAtlasSprite particleSprite) {
				this.particle = particleSprite;
				return this;
			}

			public Builder addQuads(RenderTypeGroup renderTypes, BakedQuad... quadsToAdd) {
				flushQuads(renderTypes);
				Collections.addAll(quads, quadsToAdd);
				return this;
			}

			public Builder addQuads(RenderTypeGroup renderTypes, Collection<BakedQuad> quadsToAdd) {
				flushQuads(renderTypes);
				quads.addAll(quadsToAdd);
				return this;
			}

			public BakedModel build() {
				if (quads.size() > 0) {
					addLayer(lastRenderTypes, quads);
				}
				var childrenBuilder = ImmutableMap.<String, BakedModel>builder();
				var itemPassesBuilder = ImmutableList.<BakedModel>builder();
				int i = 0;
				for (var model : this.children) {
					childrenBuilder.put("model_" + (i++), model);
					itemPassesBuilder.add(model);
				}
				return new Baked(isGui3d, isSideLit, isAmbientOcclusion, particle, transforms, overrides, childrenBuilder.build(), itemPassesBuilder.build());
			}
		}

	}

	/**
	 * A model data container which stores data for child components.
	 */
	public static class Data {
		public static final ModelProperty<Data> PROPERTY = new ModelProperty<>();

		private final Map<String, ModelData> partData;

		private Data(Map<String, ModelData> partData) {
			this.partData = partData;
		}

		@Nullable
		public ModelData get(String name) {
			return partData.get(name);
		}

		/**
		 * Helper to get the data from a {@link ModelData} instance.
		 *
		 * @param modelData The object to get data from
		 * @param name      The name of the part to get data for
		 * @return The data for the part, or the one passed in if not found
		 */
		public static ModelData resolve(ModelData modelData, String name) {
			var compositeData = modelData.get(PROPERTY);
			if (compositeData == null)
				return modelData;
			var partData = compositeData.get(name);
			return partData != null ? partData : modelData;
		}

		public static Builder builder() {
			return new Builder();
		}

		public static final class Builder {
			private final Map<String, ModelData> partData = new IdentityHashMap<>();

			public Builder with(String name, ModelData data) {
				partData.put(name, data);
				return this;
			}

			public Data build() {
				return new Data(partData);
			}
		}
	}

	public static final class Loader implements IGeometryLoader<CompositeModel> {
		public static final Loader INSTANCE = new Loader();

		private Loader() {}

		@Override
		public CompositeModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) {
			List<String> itemPasses = new ArrayList<>();
			ImmutableMap.Builder<String, BlockModel> childrenBuilder = ImmutableMap.builder();
			readChildren(jsonObject, "children", deserializationContext, childrenBuilder, itemPasses, false);
			boolean logWarning = readChildren(jsonObject, "parts", deserializationContext, childrenBuilder, itemPasses, true);

			var children = childrenBuilder.build();
			if (children.isEmpty())
				throw new JsonParseException("Composite model requires a \"children\" element with at least one element.");

			if (jsonObject.has("item_render_order")) {
				itemPasses.clear();
				for (var element : jsonObject.getAsJsonArray("item_render_order")) {
					var name = element.getAsString();
					if (!children.containsKey(name))
						throw new JsonParseException("Specified \"" + name + "\" in \"item_render_order\", but that is not a child of this model.");
					itemPasses.add(name);
				}
			}

			return new CompositeModel(children, ImmutableList.copyOf(itemPasses), logWarning);
		}

		private boolean readChildren(JsonObject jsonObject, String name, JsonDeserializationContext deserializationContext, ImmutableMap.Builder<String, BlockModel> children, List<String> itemPasses, boolean logWarning) {
			if (!jsonObject.has(name))
				return false;
			var childrenJsonObject = jsonObject.getAsJsonObject(name);
			for (Map.Entry<String, JsonElement> entry : childrenJsonObject.entrySet()) {
				children.put(entry.getKey(), deserializationContext.deserialize(entry.getValue(), BlockModel.class));
				itemPasses.add(entry.getKey()); // We can do this because GSON preserves ordering during deserialization
			}
			return logWarning;
		}
	}
}
