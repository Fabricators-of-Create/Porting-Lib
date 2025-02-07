package io.github.fabricators_of_create.porting_lib.mixin.client;

import io.github.fabricators_of_create.porting_lib.event.client.RenderArmCallback;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public class PlayerRendererMixin {
	@Inject(method = "renderLeftHand", at = @At("HEAD"), cancellable = true)
	private void onRenderLeftArm(MatrixStack poseStack, VertexConsumerProvider buffer, int packedLight, AbstractClientPlayerEntity player, CallbackInfo ci) {
		if (RenderArmCallback.EVENT.invoker().onRenderArm(poseStack, buffer, packedLight, player, Arm.LEFT))
			ci.cancel();
	}

	@Inject(method = "renderRightHand", at = @At("HEAD"), cancellable = true)
	private void onRenderRightArm(MatrixStack poseStack, VertexConsumerProvider buffer, int packedLight, AbstractClientPlayerEntity player, CallbackInfo ci) {
		if (RenderArmCallback.EVENT.invoker().onRenderArm(poseStack, buffer, packedLight, player, Arm.RIGHT))
			ci.cancel();
	}
}
