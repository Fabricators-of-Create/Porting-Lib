package io.github.fabricators_of_create.porting_lib.model_loader.extensions;

import java.util.function.Function;

import io.github.fabricators_of_create.porting_lib.model_loader.model.geometry.BlockGeometryBakingContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;

@Environment(EnvType.CLIENT)
public interface BlockModelExtensions {
  default BlockGeometryBakingContext getGeometry() {
	  throw new RuntimeException("this should be overridden via mixin. what?");
  }

	default ItemOverrides getOverrides(ModelBaker pModelBaker, BlockModel pModel, Function<Material, TextureAtlasSprite> textureGetter) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
