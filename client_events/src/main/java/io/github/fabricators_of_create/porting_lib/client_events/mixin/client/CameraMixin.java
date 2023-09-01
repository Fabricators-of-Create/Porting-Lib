package io.github.fabricators_of_create.porting_lib.client_events.mixin.client;

import io.github.fabricators_of_create.porting_lib.client_events.extensions.CameraExtensions;
import net.minecraft.client.Camera;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Camera.class)
public class CameraMixin implements CameraExtensions {
	@Shadow
	private float yRot;

	@Shadow
	private float xRot;

	@Unique
	@Override
	public void setAnglesInternal(float yaw, float pitch) {
		this.yRot = yaw;
		this.xRot = pitch;
	}
}
