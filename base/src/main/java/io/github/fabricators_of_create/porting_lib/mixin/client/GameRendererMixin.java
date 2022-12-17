package io.github.fabricators_of_create.porting_lib.mixin.client;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.blaze3d.vertex.PoseStack;

import com.mojang.math.Vector3f;

import io.github.fabricators_of_create.porting_lib.event.client.CameraSetupCallback;
import io.github.fabricators_of_create.porting_lib.event.client.CameraSetupCallback.CameraInfo;
import io.github.fabricators_of_create.porting_lib.event.client.FieldOfViewEvents;
import net.minecraft.client.Camera;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.blaze3d.shaders.Program;
import com.mojang.datafixers.util.Pair;

import io.github.fabricators_of_create.porting_lib.event.client.RegisterShadersCallback;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceManager;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
	@Shadow
	@Final
	private Camera mainCamera;

	@Inject(method = "reloadShaders", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;shutdownShaders()V"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void port_lib$registerShaders(ResourceManager manager, CallbackInfo ci, List<Program> list, List<Pair<ShaderInstance, Consumer<ShaderInstance>>> shaderRegistry) {
		try {
			RegisterShadersCallback.EVENT.invoker().onShaderReload(manager, new RegisterShadersCallback.ShaderRegistry(shaderRegistry));
		} catch (IOException e) {
			throw new RuntimeException("[Porting Lib] failed to reload modded shaders", e);
		}
	}

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
		CameraInfo info = new CameraInfo((GameRenderer) (Object) this, cam, partialTicks, cam.getYRot(), cam.getXRot(), 0);
		CameraSetupCallback.EVENT.invoker().onCameraSetup(info);
		cam.setAnglesInternal(info.yaw, info.pitch);
		poseStack.mulPose(Vector3f.ZP.rotationDegrees(info.roll));
	}
}
