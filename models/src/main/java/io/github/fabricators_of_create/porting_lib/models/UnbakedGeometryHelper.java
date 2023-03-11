package io.github.fabricators_of_create.porting_lib.models;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;

public class UnbakedGeometryHelper {
	public static void bakeElements(List<BakedQuad> quads, List<BlockElement> elements, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ResourceLocation modelLocation) {
		for (BlockElement element : elements) {
			element.faces.forEach((side, face) -> {
				var sprite = spriteGetter.apply(new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(face.texture)));
				quads.add(BlockModel.FACE_BAKERY.bakeQuad(element.from, element.to, face, sprite, side, modelState, element.rotation, element.shade, modelLocation));
			});
		}
	}

	/**
	 * Bakes a list of {@linkplain BlockElement block elements} and returns the list of baked quads.
	 */
	public static List<BakedQuad> bakeElements(List<BlockElement> elements, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ResourceLocation modelLocation) {
		if (elements.isEmpty())
			return List.of();
		var list = new ArrayList<BakedQuad>();
		bakeElements(list, elements, spriteGetter, modelState, modelLocation);
		return list;
	}
}
