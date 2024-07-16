package io.github.fabricators_of_create.porting_lib.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import io.github.fabricators_of_create.porting_lib.event.client.FieldOfViewEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
	@ModifyReturnValue(
			method = "getFov",
			at = @At(value = "RETURN", ordinal = 1) // skip the early exit
	)
	private double port_lib$modifyFov(double fov,
									Camera camera, float partialTicks, boolean usedFovSetting) {
		// returns original if not changed, this is safe
		return FieldOfViewEvents.COMPUTE.invoker().getFov((GameRenderer) (Object) this, camera, partialTicks, usedFovSetting, fov);
	}
}
