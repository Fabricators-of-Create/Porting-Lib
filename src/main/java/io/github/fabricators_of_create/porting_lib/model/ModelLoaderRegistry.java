package io.github.fabricators_of_create.porting_lib.model;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Transformation;

import io.github.fabricators_of_create.porting_lib.extensions.TransformationExtensions;
import io.github.fabricators_of_create.porting_lib.mixin.client.accessor.BlockModelAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;

public class ModelLoaderRegistry {
	private static final Map<ResourceLocation, IModelLoader<?>> loaders = Maps.newHashMap();

	public static void init() {
		registerLoader(new ResourceLocation("minecraft","elements"), VanillaProxy.Loader.INSTANCE);
		registerLoader(new ResourceLocation("forge","bucket"), DynamicBucketModel.Loader.INSTANCE);
		registerLoader(new ResourceLocation("forge","item-layers"), ItemLayerModel.Loader.INSTANCE);
	}

	public static void registerLoader(ResourceLocation id, IModelLoader<?> loader) {
		synchronized (loaders) {
			loaders.put(id, loader);
			((ReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener(loader);
		}
	}

	public static IModelGeometry<?> getModel(ResourceLocation loaderId, JsonDeserializationContext deserializationContext, JsonObject data) {
		try {
			if (!loaders.containsKey(loaderId)) {
				throw new IllegalStateException(String.format("Model loader '%s' not found. Registered loaders: %s", loaderId,
						loaders.keySet().stream().map(ResourceLocation::toString).collect(Collectors.joining(", "))));
			}

			IModelLoader<?> loader = loaders.get(loaderId);

			return loader.read(deserializationContext, data);
		} catch (Exception e) {
//			e.printStackTrace();
			throw e;
		}
	}

	@Nullable
	public static ModelState deserializeModelTransforms(JsonDeserializationContext deserializationContext, JsonObject modelData) {
		if (!modelData.has("transform"))
			return null;

		return deserializeTransform(deserializationContext, modelData.get("transform")).orElse(null);
	}

	public static Optional<ModelState> deserializeTransform(JsonDeserializationContext context, JsonElement transformData) {
		if (!transformData.isJsonObject()) {
			try {
				Transformation base = context.deserialize(transformData, Transformation.class);
				return Optional.of(new SimpleModelState(ImmutableMap.of(), ((TransformationExtensions) (Object) base).blockCenterToCorner()));
			} catch (JsonParseException e) {
				throw new JsonParseException("transform: expected a string, object or valid base transformation, got: " + transformData);
			}
		} else {
			JsonObject transform = transformData.getAsJsonObject();
			EnumMap<TransformType, Transformation> transforms = Maps.newEnumMap(ItemTransforms.TransformType.class);

			deserializeTRSR(context, transforms, transform, "thirdperson", ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
			deserializeTRSR(context, transforms, transform, "thirdperson_righthand", ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND);
			deserializeTRSR(context, transforms, transform, "thirdperson_lefthand", ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND);

			deserializeTRSR(context, transforms, transform, "firstperson", ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND);
			deserializeTRSR(context, transforms, transform, "firstperson_righthand", ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND);
			deserializeTRSR(context, transforms, transform, "firstperson_lefthand", ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND);

			deserializeTRSR(context, transforms, transform, "head", ItemTransforms.TransformType.HEAD);
			deserializeTRSR(context, transforms, transform, "gui", ItemTransforms.TransformType.GUI);
			deserializeTRSR(context, transforms, transform, "ground", ItemTransforms.TransformType.GROUND);
			deserializeTRSR(context, transforms, transform, "fixed", ItemTransforms.TransformType.FIXED);

			int k = transform.entrySet().size();
			if (transform.has("matrix")) k--;
			if (transform.has("translation")) k--;
			if (transform.has("rotation")) k--;
			if (transform.has("scale")) k--;
			if (transform.has("post-rotation")) k--;
			if (transform.has("origin")) k--;
			if (k > 0) {
				throw new JsonParseException("transform: allowed keys: 'thirdperson', 'firstperson', 'gui', 'head', 'matrix', 'translation', 'rotation', 'scale', 'post-rotation', 'origin'");
			}
			Transformation base = Transformation.identity();
			if (!transform.entrySet().isEmpty()) {
				base = context.deserialize(transform, Transformation.class);
			}
			ModelState state = new SimpleModelState(Maps.immutableEnumMap(transforms), base);
			return Optional.of(state);
		}
	}

	private static void deserializeTRSR(JsonDeserializationContext context, EnumMap<ItemTransforms.TransformType, Transformation> transforms, JsonObject transform, String name, ItemTransforms.TransformType itemCameraTransform) {
		if (transform.has(name)) {
			Transformation t = context.deserialize(transform.remove(name), Transformation.class);
			transforms.put(itemCameraTransform, ((TransformationExtensions) (Object) t).blockCenterToCorner());
		}
	}

	@Nullable
	public static IModelGeometry<?> deserializeGeometry(JsonDeserializationContext deserializationContext, JsonObject object) {
		if (!object.has("loader")) {
			return null;
		}

		ResourceLocation loader = new ResourceLocation(GsonHelper.getAsString(object, "loader"));
		return getModel(loader, deserializationContext, object);
	}

	public static class VanillaProxy implements ISimpleModelGeometry<VanillaProxy> {
		private final List<BlockElement> elements;

		public VanillaProxy(List<BlockElement> list) {
			this.elements = list;
		}

		@Override
		public void addQuads(IModelConfiguration owner, IModelBuilder<?> modelBuilder, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation) {
			for (BlockElement blockpart : elements) {
				for (Direction direction : blockpart.faces.keySet()) {
					BlockElementFace blockpartface = blockpart.faces.get(direction);
					TextureAtlasSprite textureatlassprite1 = spriteGetter.apply(owner.resolveTexture(blockpartface.texture));
					if (blockpartface.cullForDirection == null) {
						modelBuilder.addGeneralQuad(BlockModelAccessor.port_lib$bakeFace(blockpart, blockpartface, textureatlassprite1, direction, modelTransform, modelLocation));
					} else {
						modelBuilder.addFaceQuad(
								((TransformationExtensions) (Object) modelTransform.getRotation()).rotateTransform(blockpartface.cullForDirection),
								BlockModelAccessor.port_lib$bakeFace(blockpart, blockpartface, textureatlassprite1, direction, modelTransform, modelLocation));
					}
				}
			}
		}

		@Override
		public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
			Set<Material> textures = Sets.newHashSet();

			for (BlockElement part : elements) {
				for (BlockElementFace face : part.faces.values()) {
					Material texture = owner.resolveTexture(face.texture);
					if (Objects.equals(texture, MissingTextureAtlasSprite.getLocation().toString())) {
						missingTextureErrors.add(Pair.of(face.texture, owner.getModelName()));
					}

					textures.add(texture);
				}
			}

			return textures;
		}

		public static class Loader implements IModelLoader<VanillaProxy> {
			public static final Loader INSTANCE = new Loader();

			private Loader() {
			}

			@Override
			public void onResourceManagerReload(ResourceManager resourceManager) {

			}

			@Override
			public VanillaProxy read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
				List<BlockElement> list = this.getModelElements(deserializationContext, modelContents);
				return new VanillaProxy(list);
			}

			private List<BlockElement> getModelElements(JsonDeserializationContext deserializationContext, JsonObject object) {
				List<BlockElement> list = Lists.newArrayList();
				if (object.has("elements")) {
					for (JsonElement jsonelement : GsonHelper.getAsJsonArray(object, "elements")) {
						list.add(deserializationContext.deserialize(jsonelement, BlockElement.class));
					}
				}

				return list;
			}
		}
	}
}
