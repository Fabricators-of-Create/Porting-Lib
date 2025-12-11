package io.github.fabricators_of_create.porting_lib.client_events.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;

import com.mojang.blaze3d.systems.RenderSystem;

import io.github.fabricators_of_create.porting_lib.client_events.ClientEventHooks;
import io.github.fabricators_of_create.porting_lib.client_events.event.client.ViewportEvent;
import net.minecraft.client.Camera;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.world.level.material.FogType;

import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {
	@ModifyReturnValue(method = "computeFogColor", at = @At("RETURN"))
	private static Vector4f computeFogColorEvent(Vector4f original, Camera p_423439_, float p_423466_, ClientLevel p_423475_, int p_423484_, float p_423652_) {
		return ClientEventHooks.getFogColor(p_423439_, p_423466_, p_423475_, p_423484_, p_423652_, original.x, original.y, original.z);
	}

	@Inject(method = "setupFog", at = @At("TAIL"))
	private static void port_lib$setupFogEvent(Camera camera, FogRenderer.FogMode fogMode, float farPlaneDistance, boolean shouldCreateFog, float partialTick, CallbackInfo ci, @Local FogType fogType, @Local FogRenderer.FogData fogData) {
		ViewportEvent.RenderFog event = new ViewportEvent.RenderFog(fogMode, fogType, camera, partialTick, fogData.start, fogData.end, fogData.shape);
		event.sendEvent();

		if (event.isCanceled()) {
			RenderSystem.setShaderFogStart(event.getNearPlaneDistance());
			RenderSystem.setShaderFogEnd(event.getFarPlaneDistance());
			RenderSystem.setShaderFogShape(event.getFogShape());
		}
	}
}
