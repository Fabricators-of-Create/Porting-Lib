package io.github.fabricators_of_create.porting_lib.models_v2.geometry.extensions;

import com.mojang.math.Transformation;

import io.github.fabricators_of_create.porting_lib.models_v2.geometry.BlockGeometryBakingContext;
import io.github.fabricators_of_create.porting_lib.models_v2.geometry.IUnbakedGeometry;
import io.github.fabricators_of_create.porting_lib.models_v2.geometry.VisibilityData;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;

import java.util.function.Function;

public interface BlockModelExtensions {
	default ItemOverrides getPortingLibOverrides(ModelBaker pModelBakery, BlockModel pModel, Function<Material, TextureAtlasSprite> textureGetter) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default void setPortingLibCustomGeometry(IUnbakedGeometry<?> geometry) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default IUnbakedGeometry<?> getPortingLibCustomGeometry() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default boolean isPortingLibComponentVisible(String part, boolean fallback) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default VisibilityData getPortingLibVisibilityData() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default Transformation getPortingLibRootTransform() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default void setPortingLibRootTransform(Transformation rootTransform) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default BlockGeometryBakingContext getPortingLibCustomData() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
