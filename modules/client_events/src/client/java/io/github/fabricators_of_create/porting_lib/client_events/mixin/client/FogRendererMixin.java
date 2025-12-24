package io.github.fabricators_of_create.porting_lib.client_events.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import io.github.fabricators_of_create.porting_lib.client_events.ClientEventHooks;
import net.minecraft.client.Camera;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.client.renderer.fog.environment.FogEnvironment;
import net.minecraft.world.level.material.FogType;

import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {
	@ModifyReturnValue(method = "computeFogColor", at = @At("RETURN"))
	private static Vector4f computeFogColorEvent(Vector4f original, Camera p_423439_, float p_423466_, ClientLevel p_423475_, int p_423484_, float p_423652_) {
		return ClientEventHooks.getFogColor(p_423439_, p_423466_, p_423475_, p_423484_, p_423652_, original.x, original.y, original.z);
	}

	@Inject(method = "setupFog", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/fog/environment/FogEnvironment;setupFog(Lnet/minecraft/client/renderer/fog/FogData;Lnet/minecraft/client/Camera;Lnet/minecraft/client/multiplayer/ClientLevel;FLnet/minecraft/client/DeltaTracker;)V", shift = At.Shift.AFTER))
	private void captureChosenEnvironment(Camera camera, int renderDistance, DeltaTracker deltaTracker, float f, ClientLevel clientLevel, CallbackInfoReturnable<Vector4f> cir, @Local FogEnvironment fogEnvironment, @Share("env") LocalRef<FogEnvironment> chosenEnvRef) {
		chosenEnvRef.set(fogEnvironment);
	}

	@Inject(method = "setupFog", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/fog/FogData;renderDistanceEnd:F", ordinal = 0, shift = At.Shift.AFTER))
	private void modifyFogRenderEvent(Camera camera, int renderDistance, DeltaTracker deltaTracker, float f, ClientLevel clientLevel, CallbackInfoReturnable<Vector4f> cir, @Share("env") LocalRef<FogEnvironment> chosenEnvRef, @Local FogType fogType, @Local(ordinal = 1) float partialTick, @Local FogData fogData) {
		ClientEventHooks.onSetupFog(chosenEnvRef.get(), fogType, camera, renderDistance, partialTick, fogData);
	}
}
