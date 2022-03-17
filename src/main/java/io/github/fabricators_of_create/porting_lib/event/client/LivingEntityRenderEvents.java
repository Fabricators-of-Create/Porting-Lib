package io.github.fabricators_of_create.porting_lib.event.client;

import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;

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
		boolean beforeRender(LivingEntity entity, LivingEntityRenderer<?, ?> renderer, float partialRenderTick, PoseStack matrixStack, MultiBufferSource buffers, int light);
	}

	@FunctionalInterface
	interface Post {
		void afterRender(LivingEntity entity, LivingEntityRenderer<?, ?> renderer, float partialRenderTick, PoseStack matrixStack, MultiBufferSource buffers, int light);
	}
}
