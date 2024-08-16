package io.github.fabricators_of_create.porting_lib.models.geometry.extensions;

import com.mojang.math.Transformation;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import io.github.fabricators_of_create.porting_lib.models.geometry.BlockGeometryBakingContext;
import io.github.fabricators_of_create.porting_lib.models.geometry.IUnbakedGeometry;
import io.github.fabricators_of_create.porting_lib.models.geometry.VisibilityData;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;

import java.util.function.Function;

public interface BlockModelExtensions {
	default BlockGeometryBakingContext port_lib$getCustomData() {
		throw PortingLib.createMixinException("BlockModelExtensions#port_lib$getCustomData()");
	}

	default ItemOverrides getOverrides(ModelBaker bakery, BlockModel model, Function<Material, TextureAtlasSprite> spriteGetter) {
		throw PortingLib.createMixinException("BlockModelExtensions#getOverrides(ModelBaker, BlockModel, Function<Material, TextureAtlasSprite>)");
	}
}
