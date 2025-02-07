package io.github.fabricators_of_create.porting_lib.event.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.hit.HitResult;

/**
 * Events called when rendering outlines. For both, return true to cancel further processing.
 */
public interface DrawSelectionEvents {
	Event<Block> BLOCK = EventFactory.createArrayBacked(Block.class, callbacks -> (context, info, target, partialTicks, matrix, buffers) -> {
		for (Block e : callbacks)
			if (e.onHighlightBlock(context, info, target, partialTicks, matrix, buffers))
				return true;
		return false;
	});

	Event<Entity> ENTITY = EventFactory.createArrayBacked(Entity.class, callbacks -> (context, info, target, partialTicks, matrix, buffers) -> {
		for (Entity e : callbacks)
			if (e.onHighlightEntity(context, info, target, partialTicks, matrix, buffers))
				return true;
		return false;
	});

	@FunctionalInterface
	interface Block {
		boolean onHighlightBlock(WorldRenderer context, Camera info, HitResult target, float partialTicks, MatrixStack matrix, VertexConsumerProvider buffers);
	}

	@FunctionalInterface
	interface Entity {
		boolean onHighlightEntity(WorldRenderer context, Camera info, HitResult target, float partialTicks, MatrixStack matrix, VertexConsumerProvider buffers);
	}
}
