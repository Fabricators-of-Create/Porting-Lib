package io.github.fabricators_of_create.porting_lib.model;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

import com.mojang.datafixers.util.Pair;

import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

public interface ISimpleModelGeometry<T extends ISimpleModelGeometry<T>> extends IUnbakedGeometry<T> {
	@Override
	default BakedModel bake(IGeometryBakingContext owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
		TextureAtlasSprite particle = spriteGetter.apply(owner.resolveTexture("particle"));

		IModelBuilder<?> builder = IModelBuilder.of(owner, overrides, particle);

		addQuads(owner, builder, bakery, spriteGetter, modelTransform, modelLocation);

		return builder.build();
	}

	void addQuads(IGeometryBakingContext owner, IModelBuilder<?> modelBuilder, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation);

	@Override
	Collection<Material> getTextures(IGeometryBakingContext owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors);
}
