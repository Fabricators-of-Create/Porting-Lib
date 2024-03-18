package io.github.fabricators_of_create.porting_lib.models;

import net.fabricmc.fabric.api.blockview.v2.FabricBlockView;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;

public interface CustomParticleIconModel {
	/**
	 * An extension of {@link BakedModel#getParticleIcon()} that accepts custom BlockEntity data for context.
	 * The data is retrieved from {@link FabricBlockView#getBlockEntityRenderData(BlockPos)}.
	 * By default, defers to the context-less method.
	 */
	default TextureAtlasSprite getParticleIcon(Object data) {
		return ((BakedModel) this).getParticleIcon();
	}
}
