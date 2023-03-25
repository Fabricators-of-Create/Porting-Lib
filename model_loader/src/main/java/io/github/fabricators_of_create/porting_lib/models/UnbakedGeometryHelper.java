package io.github.fabricators_of_create.porting_lib.models;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper for dealing with unbaked models and geometries.
 */
public class UnbakedGeometryHelper {
	private static final ItemModelGenerator ITEM_MODEL_GENERATOR = new ItemModelGenerator();
	private static final FaceBakery FACE_BAKERY = new FaceBakery();

	/**
	 * Explanation:
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

	/**
	 * Resolves a material that may have been defined with a filesystem path instead of a proper {@link ResourceLocation}.
	 * <p>
	 * The target atlas will always be {@link TextureAtlas#LOCATION_BLOCKS}.
	 */
	public static Material resolveDirtyMaterial(@Nullable String tex, BlockModel owner) {
		if (tex == null)
			return new Material(TextureAtlas.LOCATION_BLOCKS, MissingTextureAtlasSprite.getLocation());
		if (tex.startsWith("#"))
			return owner.getMaterial(tex);

		// Attempt to convert a common (windows/linux/mac) filesystem path to a ResourceLocation.
		// This makes no promises, if it doesn't work, too bad, fix your mtl file.
		Matcher match = FILESYSTEM_PATH_TO_RESLOC.matcher(tex);
		if (match.matches()) {
			String namespace = match.group("namespace");
			String path = match.group("path").replace("\\", "/");
			tex = namespace != null ? namespace + ":" + path : path;
		}

		return new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(tex));
	}

	/**
	 * Turns a single {@link BlockElementFace} into a {@link BakedQuad}.
	 */
	public static BakedQuad bakeElementFace(BlockElement element, BlockElementFace face, TextureAtlasSprite sprite, Direction direction, ModelState state, ResourceLocation modelLocation) {
		return FACE_BAKERY.bakeQuad(element.from, element.to, face, sprite, direction, state, element.rotation, element.shade, modelLocation);
	}

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

	/**
	 * Creates a list of {@linkplain BlockElement block elements} in the shape of the specified sprite contents.
	 * These can later be baked using the same, or another texture.
	 * <p>
	 * The {@link Direction#NORTH} and {@link Direction#SOUTH} faces take up the whole surface.
	 */
	public static List<BlockElement> createUnbakedItemElements(int layerIndex, SpriteContents spriteContents) {
		return ITEM_MODEL_GENERATOR.processFrames(layerIndex, "layer" + layerIndex, spriteContents);
	}
}
