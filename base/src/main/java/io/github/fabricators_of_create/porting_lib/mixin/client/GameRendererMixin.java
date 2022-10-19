package io.github.fabricators_of_create.porting_lib.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import io.github.fabricators_of_create.porting_lib.event.client.CameraSetupCallback;
import io.github.fabricators_of_create.porting_lib.event.client.CameraSetupCallback.CameraInfo;
import io.github.fabricators_of_create.porting_lib.event.client.FOVModifierCallback;
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

	@Inject(method = "getFov", at = @At(value = "RETURN", ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private void port_lib$modifyFOV(Camera activeRenderInfo, float partialTicks, boolean useFOVSetting, CallbackInfoReturnable<Double> cir, double oldFov) {
		double newFov = FOVModifierCallback.PARTIAL_FOV.invoker().getNewFOV((GameRenderer) (Object) this, activeRenderInfo, partialTicks, oldFov);
		if (newFov != oldFov)
			cir.setReturnValue(newFov);
	}

	@Inject(method = "renderLevel", at = @At(value = "INVOKE", shift = Shift.AFTER, target = "Lnet/minecraft/client/Camera;setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V"))
	private void port_lib$modifyCameraInfo(float partialTicks, long l, PoseStack poseStack, CallbackInfo ci) {
		Camera cam = this.mainCamera;
		CameraInfo info = new CameraInfo((GameRenderer) (Object) this, cam, partialTicks, cam.getYRot(), cam.getXRot(), 0);
		CameraSetupCallback.EVENT.invoker().onCameraSetup(info);
		cam.setAnglesInternal(info.yaw, info.pitch);
		poseStack.mulPose(Vector3f.ZP.rotationDegrees(info.roll));
	}
}
