package io.github.fabricators_of_create.porting_lib.mixin.client;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import io.github.fabricators_of_create.porting_lib.event.client.RenderHandCallback;
import io.github.fabricators_of_create.porting_lib.item.ReequipAnimationItem;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

@Environment(EnvType.CLIENT)
@Mixin(HeldItemRenderer.class)
public abstract class ItemInHandRendererMixin {
	@Shadow
	private ItemStack mainHandItem;
	@Shadow
	private ItemStack offHandItem;

	@Unique
	private static int port_lib$mainHandSlot = 0;

	@Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
	private void port_lib$renderArmWithItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack stack, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
		RenderHandCallback.RenderHandEvent event = new RenderHandCallback.RenderHandEvent(player, hand, stack, matrices, vertexConsumers, tickDelta, pitch, swingProgress, equipProgress, light);
		RenderHandCallback.EVENT.invoker().onRenderHand(event);
		if (event.isCanceled()) {
			ci.cancel();
		}
	}

	@Inject(
			method = "tick",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/player/LocalPlayer;getAttackStrengthScale(F)F",
					shift = At.Shift.AFTER
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	public void port_lib$tick(CallbackInfo ci,
							ClientPlayerEntity clientPlayerEntity, ItemStack itemStack, ItemStack itemStack2) {
		if (!port_lib$shouldCauseReequipAnimation(mainHandItem, itemStack, clientPlayerEntity.getInventory().selectedSlot)) {
			mainHandItem = itemStack;
		}

		if (!port_lib$shouldCauseReequipAnimation(offHandItem, itemStack2, -1)) {
			offHandItem = itemStack2;
		}
	}

	@Unique
	private static boolean port_lib$shouldCauseReequipAnimation(@NotNull ItemStack from, @NotNull ItemStack to, int slot) {
		if (!from.isEmpty() && !to.isEmpty()) {
			boolean changed = false;
			if (slot != -1) {
				changed = slot != port_lib$mainHandSlot;
				port_lib$mainHandSlot = slot;
			}

			if (from.getItem() instanceof ReequipAnimationItem handler) {
				return handler.shouldCauseReequipAnimation(from, to, changed);
			}
		}
		return true;
	}
}
