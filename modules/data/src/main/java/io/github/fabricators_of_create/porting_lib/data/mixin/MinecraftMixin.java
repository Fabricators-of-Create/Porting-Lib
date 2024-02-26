package io.github.fabricators_of_create.porting_lib.data.mixin;

import io.github.fabricators_of_create.porting_lib.data.extensions.MinecraftExtension;
import net.minecraft.client.Minecraft;

import net.minecraft.client.main.GameConfig;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin implements MinecraftExtension {
	private GameConfig port_lib$gameConfig;

	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/Util;getNanos()J"))
	private void port_lib$saveGameConfig(GameConfig gameConfig, CallbackInfo ci) {
		this.port_lib$gameConfig = gameConfig;
	}

	@Override
	public GameConfig port_lib$getGameConfig() {
		return this.port_lib$gameConfig;
	}
}
