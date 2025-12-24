package io.github.fabricators_of_create.porting_lib.client_events.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;

import io.github.fabricators_of_create.porting_lib.client_events.ClientEventHooks;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;

import net.minecraft.world.entity.Entity;

import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

	@WrapWithCondition(method = "checkEntityPostEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;clearPostEffect()V"))
	private boolean addCustomShader(GameRenderer instance, @Nullable Entity entity) {
		return !ClientEventHooks.loadEntityShader(entity, instance);
	}

	@ModifyReturnValue(method = "getFov", at = @At(value = "RETURN", ordinal = 1))
	private float invokeFovEvent(float original, Camera camera, float partialTick, boolean usedConfiguredFov) {
		return ClientEventHooks.getFieldOfView((GameRenderer) (Object) this, camera, partialTick, original, usedConfiguredFov);
	}
}
