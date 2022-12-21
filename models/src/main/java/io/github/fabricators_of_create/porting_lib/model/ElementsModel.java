package io.github.fabricators_of_create.porting_lib.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import io.github.fabricators_of_create.porting_lib.model_loader.model.IModelBuilder;

import io.github.fabricators_of_create.porting_lib.model_loader.model.SimpleModelState;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import io.github.fabricators_of_create.porting_lib.model_loader.model.geometry.IGeometryBakingContext;
import io.github.fabricators_of_create.porting_lib.model_loader.model.geometry.IGeometryLoader;
import io.github.fabricators_of_create.porting_lib.model_loader.model.geometry.SimpleUnbakedGeometry;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

/**
 * A model composed of vanilla {@linkplain BlockElement block elements}.
 */
public class ElementsModel extends SimpleUnbakedGeometry<ElementsModel> {
	private static final Logger LOGGER = LogManager.getLogger();

	private final List<BlockElement> elements;
	private final boolean deprecatedLoader;

	public ElementsModel(List<BlockElement> elements) {
		this(elements, false);
	}

	private ElementsModel(List<BlockElement> elements, boolean deprecatedLoader) {
		this.elements = elements;
		this.deprecatedLoader = deprecatedLoader;
	}

	@Override
	protected void addQuads(IGeometryBakingContext context, IModelBuilder<?> modelBuilder, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ResourceLocation modelLocation) {
		if (deprecatedLoader)
			LOGGER.warn("Model \"" + modelLocation + "\" is using the deprecated loader \"minecraft:elements\" instead of \"forge:elements\". This loader will be removed in 1.20.");

		var rootTransform = context.getRootTransform();
		if (!rootTransform.isIdentity())
			modelState = new SimpleModelState(modelState.getRotation().compose(rootTransform), modelState.isUvLocked());

		for (BlockElement element : elements) {
			for (Direction direction : element.faces.keySet()) {
				var face = element.faces.get(direction);
				var sprite = spriteGetter.apply(context.getMaterial(face.texture));
				var quad = BlockModel.bakeFace(element, face, sprite, direction, modelState, modelLocation);

				if (face.cullForDirection == null)
					modelBuilder.addUnculledFace(quad);
				else
					modelBuilder.addCulledFace(modelState.getRotation().rotateTransform(face.cullForDirection), quad);
			}
		}
	}

	public static final class Loader implements IGeometryLoader<ElementsModel> {
		public static final Loader INSTANCE = new Loader(false);
		@Deprecated(forRemoval = true, since = "1.19")
		public static final Loader INSTANCE_DEPRECATED = new Loader(true);

		private final boolean deprecated;

		private Loader(boolean deprecated) {
			this.deprecated = deprecated;
		}

		@Override
		public ElementsModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
			if (!jsonObject.has("elements"))
				throw new JsonParseException("An element model must have an \"elements\" member.");

			List<BlockElement> elements = new ArrayList<>();
			for (JsonElement element : GsonHelper.getAsJsonArray(jsonObject, "elements")) {
				elements.add(deserializationContext.deserialize(element, BlockElement.class));
			}

			return new ElementsModel(elements, deprecated);
		}
	}
}
