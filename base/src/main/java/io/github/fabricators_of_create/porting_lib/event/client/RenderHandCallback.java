package io.github.fabricators_of_create.porting_lib.event.client;

import com.mojang.blaze3d.vertex.PoseStack;

import io.github.fabricators_of_create.porting_lib.event.BaseEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

@Environment(EnvType.CLIENT)
public interface RenderHandCallback {
	/**
	 * Fired before a hand is rendered in the first person view.
	 */
	Event<RenderHandCallback> EVENT = EventFactory.createArrayBacked(RenderHandCallback.class, callbacks -> (handEvent) -> {
		for (RenderHandCallback callback : callbacks) {
			callback.onRenderHand(handEvent);
			if (handEvent.isCanceled())
				return;
		}
	});

	void onRenderHand(RenderHandEvent event);

	class RenderHandEvent extends BaseEvent {
		private final AbstractClientPlayer player;
		private final InteractionHand hand;
		private final ItemStack stack;
		private final PoseStack matrices;
		private final MultiBufferSource vertexConsumers;
		private final float tickDelta;
		private final float pitch;
		private final float swingProgress;
		private final float equipProgress;
		private final int light;

		public RenderHandEvent(AbstractClientPlayer player, InteractionHand hand, ItemStack stack, PoseStack matrices, MultiBufferSource vertexConsumers, float tickDelta, float pitch, float swingProgress, float equipProgress, int light) {
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

		public AbstractClientPlayer getPlayer() {
			return player;
		}

		public ItemStack getItemStack() {
			return stack;
		}

		public PoseStack getPoseStack() {
			return matrices;
		}

		public MultiBufferSource getMultiBufferSource() {
			return vertexConsumers;
		}

		public int getPackedLight() {
			return light;
		}

		public float getPartialTicks() {
			return tickDelta;
		}

		public InteractionHand getHand() {
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
