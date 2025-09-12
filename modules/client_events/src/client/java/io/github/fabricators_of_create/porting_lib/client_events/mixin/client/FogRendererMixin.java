package io.github.fabricators_of_create.porting_lib.client_events.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;

import com.mojang.blaze3d.systems.RenderSystem;

import io.github.fabricators_of_create.porting_lib.client_events.event.client.ViewportEvent;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;

import net.minecraft.world.level.material.FogType;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {
	@Shadow
	private static float fogRed;

	@Shadow
	private static float fogGreen;

	@Shadow
	private static float fogBlue;

	@Inject(method = "setupColor", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;clearColor(FFFF)V", ordinal = 1))
	private static void port_lib$setupColorEvent(Camera activeRenderInfo, float partialTicks, ClientLevel level, int renderDistanceChunks, float bossColorModifier, CallbackInfo ci) {
		ViewportEvent.ComputeFogColor event = new ViewportEvent.ComputeFogColor(activeRenderInfo, partialTicks, fogRed, fogGreen, fogBlue);
		event.sendEvent();

		fogRed = event.getRed();
		fogGreen = event.getGreen();
		fogBlue = event.getBlue();
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
