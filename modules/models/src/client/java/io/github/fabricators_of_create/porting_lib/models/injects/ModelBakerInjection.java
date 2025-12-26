package io.github.fabricators_of_create.porting_lib.models.injects;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;

import org.jspecify.annotations.Nullable;

import java.util.function.Function;

public interface ModelBakerInjection {
	@Nullable
	default UnbakedModel port_lib$getTopLevelModel(ModelResourceLocation location) {
		return null;
	}

	default Function<Material, TextureAtlasSprite> port_lib$getModelTextureGetter() {
		return material -> null;
	}
}
