package io.github.fabricators_of_create.porting_lib.models;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import io.github.fabricators_of_create.porting_lib.models.geometry.IGeometryLoader;
import io.github.fabricators_of_create.porting_lib.models.geometry.IUnbakedGeometry;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ElementsModel implements IUnbakedGeometry<ElementsModel> {
	private final List<BlockElement> elements;

	public ElementsModel(List<BlockElement> elements) {
		this.elements = elements;
	}

	@Override
	public BakedModel bake(BlockModel context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation, boolean isGui3d) {
		// If there is a root transform, undo the ModelState transform, apply it, then re-apply the ModelState transform.
		// This is necessary because of things like UV locking, which should only respond to the ModelState, and as such
		// that is the only transform that should be applied during face bake.
		TextureAtlasSprite particle = spriteGetter.apply(context.getMaterial("particle"));
		SimpleBakedModel.Builder modelBuilder = new SimpleBakedModel.Builder(context.hasAmbientOcclusion(), context.getGuiLight().lightLikeBlock(), true,
				context.getTransforms(), overrides).particle(particle);
		MeshBuilder meshBuilder = RendererAccess.INSTANCE.getRenderer().meshBuilder();
		QuadEmitter emitter = meshBuilder.getEmitter();
		var postTransform = QuadTransformers.empty();
		var rootTransform = context.getRootTransform();
		if (!rootTransform.isIdentity())
			postTransform = UnbakedGeometryHelper.applyRootTransform(modelState, rootTransform);

		for (BlockElement element : elements) {
			for (Direction direction : element.faces.keySet()) {
				var face = element.faces.get(direction);
				var sprite = spriteGetter.apply(context.getMaterial(face.texture));
				var quad = BlockModel.bakeFace(element, face, sprite, direction, modelState, modelLocation);
				// Kinda hacky but should work
				emitter.fromVanilla(quad, RendererAccess.INSTANCE.getRenderer().materialFinder().find(), face.cullForDirection);
				postTransform.transform(emitter);
				quad = emitter.toBakedQuad(sprite);

				if (face.cullForDirection == null)
					modelBuilder.addUnculledFace(quad);
				else
					modelBuilder.addCulledFace(modelState.getRotation().rotateTransform(face.cullForDirection), quad);
			}
		}
		return modelBuilder.build();
	}

	public static final class Loader implements IGeometryLoader<ElementsModel> {
		public static final Loader INSTANCE = new Loader();

		private Loader() {}

		@Override
		public ElementsModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
			if (!jsonObject.has("elements"))
				throw new JsonParseException("An element model must have an \"elements\" member.");
			if (!RendererAccess.INSTANCE.hasRenderer())
				throw new JsonParseException("The Fabric Rendering API is not available. If you have Sodium, install Indium!");

			List<BlockElement> elements = new ArrayList<>();
			for (JsonElement element : GsonHelper.getAsJsonArray(jsonObject, "elements")) {
				elements.add(deserializationContext.deserialize(element, BlockElement.class));
			}

			return new ElementsModel(elements);
		}
	}
}
