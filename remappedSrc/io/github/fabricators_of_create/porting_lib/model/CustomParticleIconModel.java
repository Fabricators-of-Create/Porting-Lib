package io.github.fabricators_of_create.porting_lib.model;

import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.BlockPos;

public interface CustomParticleIconModel {
	/**
	 * An extension of {@link BakedModel#getParticleSprite()} that accepts custom BlockEntity data for context.
	 * The data is retrieved from {@link RenderAttachedBlockView#getBlockEntityRenderAttachment(BlockPos)}.
	 * By default, defers to the context-less method.
	 */
	default Sprite getParticleIcon(Object data) {
		return ((BakedModel) this).getParticleSprite();
	}
}
