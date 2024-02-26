package io.github.fabricators_of_create.porting_lib.models.geometry.extensions;

import com.mojang.math.Transformation;

import io.github.fabricators_of_create.porting_lib.models.geometry.IUnbakedGeometry;
import io.github.fabricators_of_create.porting_lib.models.geometry.VisibilityData;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;

import java.util.function.Function;

public interface BlockModelExtensions {
	default ItemOverrides getOverrides(ModelBaker pModelBakery, BlockModel pModel, Function<Material, TextureAtlasSprite> textureGetter) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default void setCustomGeometry(IUnbakedGeometry<?> geometry) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default IUnbakedGeometry<?> getCustomGeometry() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default boolean isComponentVisible(String part, boolean fallback) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default VisibilityData getVisibilityData() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default Transformation getRootTransform() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default void setRootTransform(Transformation rootTransform) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
