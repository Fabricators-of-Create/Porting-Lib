package io.github.fabricators_of_create.porting_lib.client_events.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.client_events.event.client.ViewportEvent;
import io.github.fabricators_of_create.porting_lib.client_events.injects.CameraInjection;
import net.minecraft.client.Camera;

import net.minecraft.util.Mth;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Camera.class)
public abstract class CameraMixin implements CameraInjection {
	private float port_lib$roll;

	@WrapOperation(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setRotation(FF)V", ordinal = 1))
	private void computeCameraAngles(Camera instance, float yRot, float xRot, Operation<Void> original, @Local(argsOnly = true) float partialTick) {
		var event = new ViewportEvent.ComputeCameraAngles(instance, partialTick, yRot, xRot, 0);
		event.sendEvent();

		this.port_lib$roll = event.getRoll();
		original.call(instance, event.getYaw(), event.getPitch());
	}

	@WrapOperation(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setRotation(FF)V", ordinal = 2))
	private void flipRoll(Camera instance, float yRot, float xRot, Operation<Void> original) {
		this.port_lib$roll = -this.port_lib$roll;
	}

	@ModifyArg(method = "setRotation", at = @At(value = "INVOKE", target = "Lorg/joml/Quaternionf;rotationYXZ(FFF)Lorg/joml/Quaternionf;"), index = 2)
	private float setRollValue(float value) {
		return value + (-port_lib$roll * Mth.DEG_TO_RAD);
	}

	@Override
	public float port_lib$getRoll() {
		return port_lib$roll;
	}
}
