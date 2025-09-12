package io.github.fabricators_of_create.porting_lib.client_events.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.client_events.EntityShaderManager;
import io.github.fabricators_of_create.porting_lib.client_events.event.client.ViewportEvent;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
	@Shadow
	abstract void loadEffect(ResourceLocation resourceLocation);

	@Inject(method = "checkEntityPostEffect", at = @At("TAIL"))
	private void addCustomShader(Entity entity, CallbackInfo ci) {
		if (entity != null) {
			var shader = EntityShaderManager.get(entity.getType());
			if (shader != null)
				loadEffect(shader);
		}
	}

	@ModifyReturnValue(method = "getFov", at = @At(value = "RETURN", ordinal = 1))
	private double port_lib$invokeFovEvent(double original, @Local(argsOnly = true) Camera camera, @Local(argsOnly = true) float partialTicks, @Local(argsOnly = true) boolean useConfigured) {
		var event = new ViewportEvent.ComputeFov((GameRenderer) (Object) this, camera, partialTicks, original, useConfigured);
		event.sendEvent();

		return event.getFOV();
	}
}
