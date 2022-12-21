package io.github.fabricators_of_create.porting_lib.model_loader.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.fabricators_of_create.porting_lib.model_loader.model.geometry.GeometryLoaderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;

@Mixin(Minecraft.class)
public class MinecraftMixinModelLoader {
	@Inject(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;resourceManager:Lnet/minecraft/server/packs/resources/ReloadableResourceManager;", ordinal = 0, shift = At.Shift.AFTER))
	public void port_lib$initModelRegistry(GameConfig gameConfig, CallbackInfo ci) {
		GeometryLoaderManager.init();
	}
}
