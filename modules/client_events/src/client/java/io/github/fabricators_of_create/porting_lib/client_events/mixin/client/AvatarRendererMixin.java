package io.github.fabricators_of_create.porting_lib.client_events.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;

import io.github.fabricators_of_create.porting_lib.client_events.ClientEventHooks;
import net.minecraft.client.Minecraft;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.HumanoidArm;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public class AvatarRendererMixin {
	@Inject(method = "renderLeftHand", at = @At("HEAD"), cancellable = true)
	private void onRenderLeftArm(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight, Identifier identifier, boolean bl, CallbackInfo ci) {
		if (ClientEventHooks.renderSpecificFirstPersonArm(poseStack, submitNodeCollector, packedLight, Minecraft.getInstance().player, HumanoidArm.LEFT))
			ci.cancel();
	}

	@Inject(method = "renderRightHand", at = @At("HEAD"), cancellable = true)
	private void onRenderRightArm(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight, Identifier identifier, boolean bl, CallbackInfo ci) {
		if (ClientEventHooks.renderSpecificFirstPersonArm(poseStack, submitNodeCollector, packedLight, Minecraft.getInstance().player, HumanoidArm.RIGHT))
			ci.cancel();
	}
}
