package io.github.fabricators_of_create.porting_lib.model;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelElement;
import net.minecraft.client.render.model.json.ModelElementFace;
import net.minecraft.client.render.model.json.ModelElementTexture;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.Direction;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import io.github.fabricators_of_create.porting_lib.mixin.client.accessor.BlockModelAccessor;
import io.github.fabricators_of_create.porting_lib.model.obj.OBJLoader;
import io.github.fabricators_of_create.porting_lib.util.TransformationHelper;
import org.jetbrains.annotations.Nullable;

public class ModelLoaderRegistry {
	private static final Map<Identifier, IModelLoader<?>> loaders = Maps.newHashMap();

	public static void init() {
		BlockModelAccessor.setGSON((new GsonBuilder())
				.registerTypeAdapter(JsonUnbakedModel.class, new JsonUnbakedModel.Deserializer())
				.registerTypeAdapter(ModelElement.class, new ModelElement.Deserializer())
				.registerTypeAdapter(ModelElementFace.class, new ModelElementFace.Deserializer())
				.registerTypeAdapter(ModelElementTexture.class, new ModelElementTexture.Deserializer())
				.registerTypeAdapter(Transformation.class, new Transformation.Deserializer())
				.registerTypeAdapter(ModelTransformation.class, new ModelTransformation.Deserializer())
				.registerTypeAdapter(ModelOverride.class, new ModelOverride.Deserializer())
				.registerTypeAdapter(AffineTransformation.class, new TransformationHelper.Deserializer())
				.create());
		registerLoader(new Identifier("minecraft","elements"), VanillaProxy.Loader.INSTANCE);
		registerLoader(new Identifier("forge","obj"), OBJLoader.INSTANCE);
		registerLoader(new Identifier("forge","bucket"), DynamicBucketModel.Loader.INSTANCE);
		registerLoader(new Identifier("forge","composite"), CompositeModel.Loader.INSTANCE);
		registerLoader(new Identifier("forge","item-layers"), ItemLayerModel.Loader.INSTANCE);
	}

	public static void registerLoader(Identifier id, IModelLoader<?> loader) {
		synchronized (loaders) {
			loaders.put(id, loader);
			((ReloadableResourceManagerImpl) MinecraftClient.getInstance().getResourceManager()).registerReloader(loader);
		}
	}

	public static IModelGeometry<?> getModel(Identifier loaderId, JsonDeserializationContext deserializationContext, JsonObject data) {
		try {
			if (!loaders.containsKey(loaderId)) {
				throw new IllegalStateException(String.format("Model loader '%s' not found. Registered loaders: %s", loaderId,
						loaders.keySet().stream().map(Identifier::toString).collect(Collectors.joining(", "))));
			}

			IModelLoader<?> loader = loaders.get(loaderId);

			return loader.read(deserializationContext, data);
		} catch (Exception e) {
//			e.printStackTrace();
			throw e;
		}
	}

	@Nullable
	public static ModelBakeSettings deserializeModelTransforms(JsonDeserializationContext deserializationContext, JsonObject modelData) {
		if (!modelData.has("transform"))
			return null;

		return deserializeTransform(deserializationContext, modelData.get("transform")).orElse(null);
	}

	public static Optional<ModelBakeSettings> deserializeTransform(JsonDeserializationContext context, JsonElement transformData) {
		if (!transformData.isJsonObject()) {
			try {
				AffineTransformation base = context.deserialize(transformData, AffineTransformation.class);
				return Optional.of(new SimpleModelState(ImmutableMap.of(), base.blockCenterToCorner()));
			} catch (JsonParseException e) {
				throw new JsonParseException("transform: expected a string, object or valid base transformation, got: " + transformData);
			}
		} else {
			JsonObject transform = transformData.getAsJsonObject();
			EnumMap<Mode, AffineTransformation> transforms = Maps.newEnumMap(ModelTransformation.Mode.class);

			deserializeTRSR(context, transforms, transform, "thirdperson", ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND);
			deserializeTRSR(context, transforms, transform, "thirdperson_righthand", ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND);
			deserializeTRSR(context, transforms, transform, "thirdperson_lefthand", ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND);

			deserializeTRSR(context, transforms, transform, "firstperson", ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND);
			deserializeTRSR(context, transforms, transform, "firstperson_righthand", ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND);
			deserializeTRSR(context, transforms, transform, "firstperson_lefthand", ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND);

			deserializeTRSR(context, transforms, transform, "head", ModelTransformation.Mode.HEAD);
			deserializeTRSR(context, transforms, transform, "gui", ModelTransformation.Mode.GUI);
			deserializeTRSR(context, transforms, transform, "ground", ModelTransformation.Mode.GROUND);
			deserializeTRSR(context, transforms, transform, "fixed", ModelTransformation.Mode.FIXED);

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
			AffineTransformation base = AffineTransformation.identity();
			if (!transform.entrySet().isEmpty()) {
				base = context.deserialize(transform, AffineTransformation.class);
			}
			ModelBakeSettings state = new SimpleModelState(Maps.immutableEnumMap(transforms), base);
			return Optional.of(state);
		}
	}

	private static void deserializeTRSR(JsonDeserializationContext context, EnumMap<ModelTransformation.Mode, AffineTransformation> transforms, JsonObject transform, String name, ModelTransformation.Mode itemCameraTransform) {
		if (transform.has(name)) {
			AffineTransformation t = context.deserialize(transform.remove(name), AffineTransformation.class);
			transforms.put(itemCameraTransform, t.blockCenterToCorner());
		}
	}

	@Nullable
	public static IModelGeometry<?> deserializeGeometry(JsonDeserializationContext deserializationContext, JsonObject object) {
		if (!object.has("loader")) {
			return null;
		}

		Identifier loader = new Identifier(JsonHelper.getString(object, "loader"));
		return getModel(loader, deserializationContext, object);
	}

	/* Explanation:
	 * This takes anything that looks like a valid resourcepack texture location, and tries to extract a resourcelocation out of it.
	 *  1. it will ignore anything up to and including an /assets/ folder,
	 *  2. it will take the next path component as a namespace,
	 *  3. it will match but skip the /textures/ part of the path,
	 *  4. it will take the rest of the path up to but excluding the .png extension as the resource path
	 * It's a best-effort situation, to allow model files exported by modelling software to be used without post-processing.
	 * Example:
	 *   C:\Something\Or Other\src\main\resources\assets\mymodid\textures\item\my_thing.png
	 *   ........................................--------_______----------_____________----
	 *                                                 <namespace>        <path>
	 * Result after replacing '\' to '/': mymodid:item/my_thing
	 */
	private static final Pattern FILESYSTEM_PATH_TO_RESLOC =
			Pattern.compile("(?:.*[\\\\/]assets[\\\\/](?<namespace>[a-z_-]+)[\\\\/]textures[\\\\/])?(?<path>[a-z_\\\\/-]+)\\.png");

	public static final String WHITE_TEXTURE = "porting_lib:white";

	public static SpriteIdentifier resolveTexture(@Nullable String tex, IModelConfiguration owner) {
		if (tex == null)
			return blockMaterial(WHITE_TEXTURE);
		if (tex.startsWith("#"))
			return owner.resolveTexture(tex);

		// Attempt to convert a common (windows/linux/mac) filesystem path to a ResourceLocation.
		// This makes no promises, if it doesn't work, too bad, fix your mtl file.
		Matcher match = FILESYSTEM_PATH_TO_RESLOC.matcher(tex);
		if (match.matches()) {
			String namespace = match.group("namespace");
			String path = match.group("path").replace("\\", "/");
			if (namespace != null)
				return blockMaterial(new Identifier(namespace, path));
			return blockMaterial(path);
		}

		return blockMaterial(tex);
	}

	@SuppressWarnings("deprecation")
	public static SpriteIdentifier blockMaterial(String location) {
		return new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier(location));
	}

	@SuppressWarnings("deprecation")
	public static SpriteIdentifier blockMaterial(Identifier location) {
		return new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, location);
	}

	public static class VanillaProxy implements ISimpleModelGeometry<VanillaProxy> {
		private final List<ModelElement> elements;

		public VanillaProxy(List<ModelElement> list) {
			this.elements = list;
		}

		@Override
		public void addQuads(IModelConfiguration owner, IModelBuilder<?> modelBuilder, ModelLoader bakery, Function<SpriteIdentifier, Sprite> spriteGetter, ModelBakeSettings modelTransform, Identifier modelLocation) {
			for (ModelElement blockpart : elements) {
				for (Direction direction : blockpart.faces.keySet()) {
					ModelElementFace blockpartface = blockpart.faces.get(direction);
					Sprite textureatlassprite1 = spriteGetter.apply(owner.resolveTexture(blockpartface.textureId));
					if (blockpartface.cullFace == null) {
						modelBuilder.addGeneralQuad(BlockModelAccessor.port_lib$bakeFace(blockpart, blockpartface, textureatlassprite1, direction, modelTransform, modelLocation));
					} else {
						modelBuilder.addFaceQuad(
								modelTransform.getRotation().rotateTransform(blockpartface.cullFace),
								BlockModelAccessor.port_lib$bakeFace(blockpart, blockpartface, textureatlassprite1, direction, modelTransform, modelLocation));
					}
				}
			}
		}

		@Override
		public Collection<SpriteIdentifier> getTextures(IModelConfiguration owner, Function<Identifier, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
			Set<SpriteIdentifier> textures = Sets.newHashSet();

			for (ModelElement part : elements) {
				for (ModelElementFace face : part.faces.values()) {
					SpriteIdentifier texture = owner.resolveTexture(face.textureId);
					if (Objects.equals(texture, MissingSprite.getMissingSpriteId().toString())) {
						missingTextureErrors.add(Pair.of(face.textureId, owner.getModelName()));
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
			public void reload(ResourceManager resourceManager) {

			}

			@Override
			public VanillaProxy read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
				List<ModelElement> list = this.getModelElements(deserializationContext, modelContents);
				return new VanillaProxy(list);
			}

			private List<ModelElement> getModelElements(JsonDeserializationContext deserializationContext, JsonObject object) {
				List<ModelElement> list = Lists.newArrayList();
				if (object.has("elements")) {
					for (JsonElement jsonelement : JsonHelper.getArray(object, "elements")) {
						list.add(deserializationContext.deserialize(jsonelement, ModelElement.class));
					}
				}

				return list;
			}
		}
	}
}
