package io.github.fabricators_of_create.porting_lib.model;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import net.minecraft.client.resources.model.BakedModel;

import org.jetbrains.annotations.NotNull;

public interface CustomParticleIconModel {
	default TextureAtlasSprite getParticleIcon(@NotNull Object data) {
		return ((BakedModel) this).getParticleIcon();
	}
}
