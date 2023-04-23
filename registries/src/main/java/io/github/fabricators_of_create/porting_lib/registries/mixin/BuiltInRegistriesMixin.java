package io.github.fabricators_of_create.porting_lib.registries.mixin;

import io.github.fabricators_of_create.porting_lib.registries.DynamicRegistryHandler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(BuiltInRegistries.class)
public class BuiltInRegistriesMixin {
	@Inject(method = "freeze", at = @At("RETURN"))
	private static void onFreezeBuiltins(CallbackInfo ci) {
		DynamicRegistryHandler.freeze();
	}
}
