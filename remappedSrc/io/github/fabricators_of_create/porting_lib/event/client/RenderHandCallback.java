package io.github.fabricators_of_create.porting_lib.event.client;

import io.github.fabricators_of_create.porting_lib.event.BaseEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

@Environment(EnvType.CLIENT)
public interface RenderHandCallback {
	Event<RenderHandCallback> EVENT = EventFactory.createArrayBacked(RenderHandCallback.class, callbacks -> (handEvent) -> {
		for (RenderHandCallback callback : callbacks) {
			callback.onRenderHand(handEvent);
			if (handEvent.isCanceled())
				return;
		}
	});

	void onRenderHand(RenderHandEvent event);

	class RenderHandEvent extends BaseEvent {
		private final AbstractClientPlayerEntity player;
		private final Hand hand;
		private final ItemStack stack;
		private final MatrixStack matrices;
		private final VertexConsumerProvider vertexConsumers;
		private final float tickDelta;
		private final float pitch;
		private final float swingProgress;
		private final float equipProgress;
		private final int light;

		public RenderHandEvent(AbstractClientPlayerEntity player, Hand hand, ItemStack stack, MatrixStack matrices, VertexConsumerProvider vertexConsumers, float tickDelta, float pitch, float swingProgress, float equipProgress, int light) {
			this.player = player;
			this.hand = hand;
			this.stack = stack;
			this.matrices = matrices;
			this.vertexConsumers = vertexConsumers;
			this.tickDelta = tickDelta;
			this.pitch = pitch;
			this.swingProgress = swingProgress;
			this.equipProgress = equipProgress;
			this.light = light;
		}

		public AbstractClientPlayerEntity getPlayer() {
			return player;
		}

		public ItemStack getItemStack() {
			return stack;
		}

		public MatrixStack getPoseStack() {
			return matrices;
		}

		public VertexConsumerProvider getMultiBufferSource() {
			return vertexConsumers;
		}

		public int getPackedLight() {
			return light;
		}

		public float getPartialTicks() {
			return tickDelta;
		}

		public Hand getHand() {
			return hand;
		}

		public float getPitch() {
			return pitch;
		}

		public float getEquipProgress() {
			return equipProgress;
		}

		public float getSwingProgress() {
			return swingProgress;
		}

		@Override
		public void sendEvent() {
			RenderHandCallback.EVENT.invoker().onRenderHand(this);
		}
	}
}
