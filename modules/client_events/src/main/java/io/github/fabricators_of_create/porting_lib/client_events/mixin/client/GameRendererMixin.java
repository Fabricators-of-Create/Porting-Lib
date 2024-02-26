package io.github.fabricators_of_create.porting_lib.client_events.mixin.client;

import io.github.fabricators_of_create.porting_lib.client_events.EntityShaderManager;
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
}
