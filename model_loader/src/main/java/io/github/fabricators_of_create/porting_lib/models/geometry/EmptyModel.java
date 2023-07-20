package io.github.fabricators_of_create.porting_lib.models.geometry;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

/**
 * A completely empty model with no quads or texture dependencies.
 * <p>
 * You can access it as a {@link BakedModel}, an {@link IUnbakedGeometry} or an {@link IGeometryLoader}.
 */
public class EmptyModel implements IUnbakedGeometry<EmptyModel> {
	public static final BakedModel BAKED = new Baked();
	public static final EmptyModel INSTANCE = new EmptyModel();
	public static final IGeometryLoader<EmptyModel> LOADER = (json, ctx) -> INSTANCE;

	private EmptyModel() {}

	@Override
	public BakedModel bake(BlockModel context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation, boolean isGui3d) {
		return BAKED;
	}

	@Override
	public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, BlockModel context) {
		// NO-OP
	}

	private static class Baked extends SimpleBakedModel {
		private static final Material MISSING_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, MissingTextureAtlasSprite.getLocation());

		public Baked() {
			super(List.of(), Map.of(), false, false, false, MISSING_TEXTURE.sprite(), ItemTransforms.NO_TRANSFORMS, ItemOverrides.EMPTY);
		}

		@Override
		public TextureAtlasSprite getParticleIcon() {
			return MISSING_TEXTURE.sprite();
		}
	}
}
