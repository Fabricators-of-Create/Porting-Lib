package io.github.fabricators_of_create.porting_lib.model_loader.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import io.github.fabricators_of_create.porting_lib.model_loader.client.textures.UnitTextureAtlasSprite;
import io.github.fabricators_of_create.porting_lib.model_loader.model.geometry.IGeometryBakingContext;
import io.github.fabricators_of_create.porting_lib.model_loader.model.geometry.IGeometryLoader;
import io.github.fabricators_of_create.porting_lib.model_loader.model.geometry.IUnbakedGeometry;
import io.github.fabricators_of_create.porting_lib.model_loader.model.geometry.SimpleUnbakedGeometry;
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
public class EmptyModel extends SimpleUnbakedGeometry<EmptyModel> {
	public static final BakedModel BAKED = new Baked();
	public static final EmptyModel INSTANCE = new EmptyModel();
	public static final IGeometryLoader<EmptyModel> LOADER = (json, ctx) -> INSTANCE;

	private EmptyModel() {}

	@Override
	protected void addQuads(IGeometryBakingContext owner, IModelBuilder<?> modelBuilder, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation) {
		// NO-OP
	}

	@Override
	public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
		return BAKED;
	}

	private static class Baked extends SimpleBakedModel {
		private static final Material MISSING_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, MissingTextureAtlasSprite.getLocation());

		public Baked() {
			super(List.of(), Map.of(), false, false, false, UnitTextureAtlasSprite.INSTANCE, ItemTransforms.NO_TRANSFORMS, ItemOverrides.EMPTY);
		}

		@Override
		public TextureAtlasSprite getParticleIcon() {
			return MISSING_TEXTURE.sprite();
		}
	}
}
