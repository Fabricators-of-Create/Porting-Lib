package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.event.common.ModsLoadedCallback;
import io.github.fabricators_of_create.porting_lib.util.PortingHooks;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.core.Registry;
import net.minecraft.server.Main;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Main.class)
public class MainMixin {
	@Inject(method = "main", at = @At(value = "INVOKE", target = "Lnet/minecraft/Util;startTimerHackThread()V"))
	private static void port_lib$modsLoaded(CallbackInfo ci) {
		ModsLoadedCallback.EVENT.invoker().onAllModsLoaded(EnvType.SERVER);
	}

	@Inject(method = "main", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/Bootstrap;validate()V"))
	private static void port_lib$fluidss(String[] strings, CallbackInfo ci) {
		RegistryEntryAddedCallback.event(Registry.FLUID).register((rawId, id, fluid) -> {
			PortingHooks.registerFluidVariantAttributesFromFluidAttributes(fluid, fluid.getAttributes());
		});
	}
}
