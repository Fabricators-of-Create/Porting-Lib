package io.github.fabricators_of_create.porting_lib.client_events.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;

import io.github.fabricators_of_create.porting_lib.client_events.event.client.RenderArmCallback;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;

import net.minecraft.world.entity.HumanoidArm;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin {
	@Inject(method = "renderLeftHand", at = @At("HEAD"), cancellable = true)
	private void onRenderLeftArm(PoseStack poseStack, MultiBufferSource buffer, int packedLight, AbstractClientPlayer player, CallbackInfo ci) {
		if (RenderArmCallback.EVENT.invoker().onRenderArm(poseStack, buffer, packedLight, player, HumanoidArm.LEFT))
			ci.cancel();
	}

	@Inject(method = "renderRightHand", at = @At("HEAD"), cancellable = true)
	private void onRenderRightArm(PoseStack poseStack, MultiBufferSource buffer, int packedLight, AbstractClientPlayer player, CallbackInfo ci) {
		if (RenderArmCallback.EVENT.invoker().onRenderArm(poseStack, buffer, packedLight, player, HumanoidArm.RIGHT))
			ci.cancel();
	}
}
