package io.github.fabricators_of_create.porting_lib.config.mixin.client;

import io.github.fabricators_of_create.porting_lib.config.ConfigTracker;
import io.github.fabricators_of_create.porting_lib.config.ConfigType;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;

import net.minecraft.client.main.GameConfig;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	// Inject right after the fabric entrypoint
	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/lang/Thread;currentThread()Ljava/lang/Thread;"))
	public void port_lib$modsLoaded(GameConfig gameConfig, CallbackInfo ci) {
		ConfigTracker.INSTANCE.loadConfigs(ConfigType.CLIENT, FabricLoader.getInstance().getConfigDir());
		ConfigTracker.INSTANCE.loadConfigs(ConfigType.COMMON, FabricLoader.getInstance().getConfigDir());
	}
}
