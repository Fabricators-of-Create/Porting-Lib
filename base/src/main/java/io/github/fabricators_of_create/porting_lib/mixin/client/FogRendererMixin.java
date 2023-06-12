package io.github.fabricators_of_create.porting_lib.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import com.mojang.blaze3d.systems.RenderSystem;

import io.github.fabricators_of_create.porting_lib.event.client.FogEvents;
import io.github.fabricators_of_create.porting_lib.event.client.FogEvents.ColorData;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.FogType;

@Environment(EnvType.CLIENT)
@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {
	@Shadow
	private static float fogRed;

	@Shadow
	private static float fogGreen;

	@Shadow
	private static float fogBlue;

	@ModifyArgs(method = "setupColor", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;clearColor(FFFF)V", remap = false))
	private static void modifyFogColors(Args args, Camera camera, float partialTicks, ClientLevel level, int renderDistanceChunks, float bossColorModifier) {
		ColorData data = new ColorData(camera, fogRed, fogGreen, fogBlue);
		FogEvents.SET_COLOR.invoker().setColor(data, partialTicks);
		fogRed = data.getRed();
		fogGreen = data.getGreen();
		fogBlue = data.getBlue();
	}

	@Inject(method = "setupFog", at = @At("HEAD"), cancellable = true)
	private static void setupFog(Camera camera, FogRenderer.FogMode fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci) {
		float density = FogEvents.SET_DENSITY.invoker().setDensity(camera, 0.1f);
		if (density != 0.1f) {
			RenderSystem.setShaderFogStart(-8.0F);
			RenderSystem.setShaderFogEnd(density * 0.5F);
			ci.cancel();
		}
	}

	@Inject(method = "setupFog", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
	private static void fogRenderEvent(Camera camera, FogRenderer.FogMode fogMode, float viewDistance, boolean thickFog, float partialTick, CallbackInfo ci, FogType fogType, Entity entity, FogRenderer.FogData fogData) {
		FogEvents.FogData data = new FogEvents.FogData(fogData.start, fogData.end, fogData.shape);
		if (FogEvents.RENDER_FOG.invoker().onFogRender(fogMode, fogType, camera, partialTick, viewDistance, fogData.start, fogData.end, fogData.shape, data)) {
			RenderSystem.setShaderFogStart(data.getNearPlaneDistance());
			RenderSystem.setShaderFogEnd(data.getFarPlaneDistance());
			RenderSystem.setShaderFogShape(data.getFogShape());
		}
	}
}
