package io.github.fabricators_of_create.porting_lib.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import io.github.fabricators_of_create.porting_lib.event.client.CameraSetupCallback;
import io.github.fabricators_of_create.porting_lib.event.client.FieldOfViewEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
	@Shadow
	@Final
	private Camera mainCamera;

	@ModifyReturnValue(
			method = "getFov",
			at = @At(value = "RETURN", ordinal = 1) // skip the early exit
	)
	private double port_lib$modifyFov(double fov,
									Camera camera, float partialTicks, boolean usedFovSetting) {
		// returns original if not changed, this is safe
		return FieldOfViewEvents.COMPUTE.invoker().getFov((GameRenderer) (Object) this, camera, partialTicks, usedFovSetting, fov);
	}

	@Inject(method = "renderLevel", at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/client/Camera;setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V"))
	private void port_lib$modifyCameraInfo(float partialTicks, long l, PoseStack poseStack, CallbackInfo ci) {
		Camera cam = this.mainCamera;
		CameraSetupCallback.CameraInfo info = new CameraSetupCallback.CameraInfo((GameRenderer) (Object) this, cam, partialTicks, cam.getYRot(), cam.getXRot(), 0);
		CameraSetupCallback.EVENT.invoker().onCameraSetup(info);
		cam.setAnglesInternal(info.yaw, info.pitch);
		poseStack.mulPose(Axis.ZP.rotationDegrees(info.roll));
	}
}
