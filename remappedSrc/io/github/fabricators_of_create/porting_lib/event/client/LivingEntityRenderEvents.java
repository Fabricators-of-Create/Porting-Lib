package io.github.fabricators_of_create.porting_lib.event.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

public interface LivingEntityRenderEvents {

	Event<Pre> PRE = EventFactory.createArrayBacked(Pre.class, callbacks -> (entity, renderer, partialRenderTick, matrixStack, buffers, light) -> {
		for (Pre pre : callbacks)
			if(pre.beforeRender(entity, renderer, partialRenderTick, matrixStack, buffers, light))
				return true;
		return false;
	});

	Event<Post> POST = EventFactory.createArrayBacked(Post.class, callbacks -> (entity, renderer, partialRenderTick, matrixStack, buffers, light) -> {
		for (Post post : callbacks)
			post.afterRender(entity, renderer, partialRenderTick, matrixStack, buffers, light);
	});


	@FunctionalInterface
	interface Pre {
		boolean beforeRender(LivingEntity entity, LivingEntityRenderer<?, ?> renderer, float partialRenderTick, MatrixStack matrixStack, VertexConsumerProvider buffers, int light);
	}

	@FunctionalInterface
	interface Post {
		void afterRender(LivingEntity entity, LivingEntityRenderer<?, ?> renderer, float partialRenderTick, MatrixStack matrixStack, VertexConsumerProvider buffers, int light);
	}
}
