package io.github.fabricators_of_create.porting_lib.extensions.mixin.client;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
	@Shadow
	@Nullable
	private ClientLevel level;

	@Shadow
	private int ticks;

	@Inject(method = "tickRain", at = @At("HEAD"), cancellable = true)
	private void port_lib$customRainTick(Camera camera, CallbackInfo ci) {
		if (level.effects().tickRain(level, ticks, camera))
			ci.cancel();
	}
}
