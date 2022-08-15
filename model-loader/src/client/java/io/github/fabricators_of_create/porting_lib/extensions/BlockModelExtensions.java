package io.github.fabricators_of_create.porting_lib.extensions;

import io.github.fabricators_of_create.porting_lib.model.BlockGeometryBakingContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;

import java.util.function.Function;

@Environment(EnvType.CLIENT)
public interface BlockModelExtensions {
  default BlockGeometryBakingContext getGeometry() {
	  throw new RuntimeException("this should be overridden via mixin. what?");
  }

	default ItemOverrides getOverrides(ModelBakery pModelBakery, BlockModel pModel, Function<Material, TextureAtlasSprite> textureGetter) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
