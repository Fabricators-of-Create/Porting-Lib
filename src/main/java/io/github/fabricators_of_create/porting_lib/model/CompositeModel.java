package io.github.fabricators_of_create.porting_lib.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;

import io.github.fabricators_of_create.porting_lib.render.TransformTypeDependentItemBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;

public class CompositeModel implements BakedModel, FabricBakedModel, TransformTypeDependentItemBakedModel {
	private final ImmutableMap<String, BakedModel> bakedParts;
	private final boolean isAmbientOcclusion;
	private final boolean isGui3d;
	private final boolean isSideLit;
	private final TextureAtlasSprite particle;
	private final ItemOverrides overrides;
	private final ModelState transforms;

	public CompositeModel(boolean isGui3d, boolean isSideLit, boolean isAmbientOcclusion, TextureAtlasSprite particle, ImmutableMap<String, BakedModel> bakedParts, ModelState combinedTransform, ItemOverrides overrides) {
		this.bakedParts = bakedParts;
		this.isAmbientOcclusion = isAmbientOcclusion;
		this.isGui3d = isGui3d;
		this.isSideLit = isSideLit;
		this.particle = particle;
		this.overrides = overrides;
		this.transforms = combinedTransform;
	}

//	@Nonnull TODO: PORT?
//	@Override
//	public IModelData getModelData(@Nonnull BlockAndTintGetter level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData modelData)
//	{
//		CompositeModelData composite = new CompositeModelData();
//		for(Map.Entry<String, BakedModel> entry : bakedParts.entrySet())
//		{
//			composite.putSubmodelData(entry.getKey(), entry.getValue().getModelData(level, pos, state, ModelDataWrapper.wrap(modelData)));
//		}
//		return composite;
//	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
		return Lists.newArrayList();
	}

	@Override
	public boolean useAmbientOcclusion()
	{
		return isAmbientOcclusion;
	}

	@Override
	public boolean isGui3d()
	{
		return isGui3d;
	}

	@Override
	public boolean usesBlockLight()
	{
		return isSideLit;
	}

	@Override
	public boolean isCustomRenderer()
	{
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleIcon()
	{
		return particle;
	}

	@Override
	public ItemTransforms getTransforms() {
		return ItemTransforms.NO_TRANSFORMS;
	}

	@Override
	public ItemOverrides getOverrides()
	{
		return overrides;
	}

	@Override
	public BakedModel handlePerspective(ItemTransforms.TransformType cameraTransformType, PoseStack poseStack)
	{
		return PerspectiveMapWrapper.handlePerspective(this, transforms, cameraTransformType, poseStack);
	}

	@Nullable
	public BakedModel getPart(String name)
	{
		return bakedParts.get(name);
	}

	@Override
	public boolean isVanillaAdapter() {
		return false;
	}

	@Override
	public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
		if (blockView instanceof RenderAttachedBlockView renderAttachedBlockView) {
			for (Map.Entry<String, BakedModel> entry : bakedParts.entrySet()) {
				((FabricBakedModel) entry.getValue()).emitBlockQuads(new CustomDataBlockView(renderAttachedBlockView, CompositeModelData.get((IModelData) renderAttachedBlockView.getBlockEntityRenderAttachment(pos), entry.getKey())), state, pos, randomSupplier, context);
			}
		}
	}

	@Override
	public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
		for (Map.Entry<String, BakedModel> entry : bakedParts.entrySet()) {
			((FabricBakedModel) entry.getValue()).emitItemQuads(stack, randomSupplier, context);
		}
	}

	private static class Submodel implements IModelGeometryPart
	{
		private final String name;
		private final BlockModel model;
		private final ModelState modelTransform;

		private Submodel(String name, BlockModel model, ModelState modelTransform)
		{
			this.name = name;
			this.model = model;
			this.modelTransform = modelTransform;
		}

		@Override
		public String name()
		{
			return name;
		}

		@Override
		public void addQuads(IModelConfiguration owner, IModelBuilder<?> modelBuilder, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation)
		{
			throw new UnsupportedOperationException("Attempted to call adQuads on a Submodel instance. Please don't.");
		}

		public BakedModel bakeModel(ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation)
		{
			return model.bake(bakery, spriteGetter, new CompositeModelState(this.modelTransform, modelTransform,
					this.modelTransform.isUvLocked() || modelTransform.isUvLocked()), modelLocation);
		}

		@Override
		public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
		{
			return model.getMaterials(modelGetter, missingTextureErrors);
		}
	}

	public static class Geometry implements IMultipartModelGeometry<Geometry>
	{
		private final ImmutableMap<String, Submodel> parts;

		Geometry(ImmutableMap<String, Submodel> parts)
		{
			this.parts = parts;
		}

		@Override
		public Collection<? extends IModelGeometryPart> getParts()
		{
			return parts.values();
		}

		@Override
		public Optional<? extends IModelGeometryPart> getPart(String name)
		{
			return Optional.ofNullable(parts.get(name));
		}

		@Override
		public BakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation)
		{
			Material particleLocation = owner.resolveTexture("particle");
			TextureAtlasSprite particle = spriteGetter.apply(particleLocation);

			ImmutableMap.Builder<String, BakedModel> bakedParts = ImmutableMap.builder();
			for(Map.Entry<String, Submodel> part : parts.entrySet())
			{
				Submodel submodel = part.getValue();
				if (!owner.getPartVisibility(submodel))
					continue;
				bakedParts.put(part.getKey(), submodel.bakeModel(bakery, spriteGetter, modelTransform, modelLocation));
			}
			return new CompositeModel(owner.isShadedInGui(), owner.isSideLit(), owner.useSmoothLighting(), particle, bakedParts.build(), owner.getCombinedTransform(), overrides);
		}

		@Override
		public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
		{
			Set<Material> textures = new HashSet<>();
			for(Submodel part : parts.values())
			{
				textures.addAll(part.getTextures(owner, modelGetter, missingTextureErrors));
			}
			return textures;
		}
	}

	public static class Loader implements IModelLoader<Geometry>
	{
		public static final Loader INSTANCE = new Loader();

		private Loader() {}

		@Override
		public void onResourceManagerReload(ResourceManager resourceManager)
		{

		}

		@Override
		public Geometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents)
		{
			if (!modelContents.has("parts"))
				throw new RuntimeException("Composite model requires a \"parts\" element.");
			ImmutableMap.Builder<String, Submodel> parts = ImmutableMap.builder();
			for(Map.Entry<String, JsonElement> part : modelContents.get("parts").getAsJsonObject().entrySet())
			{
				// TODO: Allow customizing state? If so, how?
				ModelState modelTransform = SimpleModelState.IDENTITY;
				parts.put(part.getKey(), new Submodel(
						part.getKey(),
						deserializationContext.deserialize(part.getValue(), BlockModel.class),
						modelTransform));
			}
			return new Geometry(parts.build());
		}
	}

	/**
	 * A model data container which stores data for child components.
	 */
	public static class CompositeModelData extends ModelDataMap {
		public static final ModelProperty<CompositeModelData> SUBMODEL_DATA = new ModelProperty<>();

		/**
		 * Helper to get the CompositeModelData from an unknown IModelData instance.
		 * @param modelData The undetermined instance to get data from
		 * @return An optional representing the composite data, if present.
		 */
		public static Optional<CompositeModelData> get(IModelData modelData) {
			return Optional.ofNullable(modelData.getData(SUBMODEL_DATA));
		}

		/**
		 * Helper to get child data from an unknown IModelData instance.
		 * @param modelData The undetermined instance to get data from
		 * @param name The name of the child part to get data for.
		 * @return The data for the child, or empty if not available.
		 */
		public static IModelData get(IModelData modelData, String name) {
			return get(modelData).map(data -> data.getSubmodelData(name))
					.orElse(EmptyModelData.INSTANCE);
		}

		// Implementation

		private final Map<String, IModelData> parts = new HashMap<>();

		public IModelData getSubmodelData(String name) {
			if (parts.containsKey(name))
				return parts.get(name);
			return EmptyModelData.INSTANCE;
		}

		public void putSubmodelData(String name, IModelData data) {
			parts.put(name, data);
		}

		@Override
		public boolean hasProperty(ModelProperty<?> prop) {
			return prop == SUBMODEL_DATA ||super.hasProperty(prop);
		}

		@SuppressWarnings("unchecked")
		@Nullable
		@Override
		public <T> T getData(ModelProperty<T> prop) {
			if (prop == SUBMODEL_DATA)
				return (T)this;
			return super.getData(prop);
		}

		@SuppressWarnings("unchecked")
		@Nullable
		@Override
		public <T> T setData(ModelProperty<T> prop, T data) {
			if (prop == SUBMODEL_DATA)
				return (T)this;
			return super.setData(prop, data);
		}
	}

	/**
	 * Wrapper for an IModelData instance which allows forwarding queries to the parent,
	 * but stores any new/modified values itself, avoiding modifications to the parent.
	 */
	private static class ModelDataWrapper extends ModelDataMap {
		private final IModelData parent;

		public static IModelData wrap(IModelData parent) {
			return new ModelDataWrapper(parent);
		}

		private ModelDataWrapper(IModelData parent) {
			this.parent = parent;
		}

		@Override
		public boolean hasProperty(ModelProperty<?> prop) {
			return super.hasProperty(prop) || parent.hasProperty(prop);
		}

		@Nullable
		@Override
		public <T> T getData(ModelProperty<T> prop) {
			return super.hasProperty(prop) ? super.getData(prop) : parent.getData(prop);
		}

		@Nullable
		@Override
		public <T> T setData(ModelProperty<T> prop, T data) {
			// We do not want to delegate setting to the parent
			return super.setData(prop, data);
		}
	}
}
