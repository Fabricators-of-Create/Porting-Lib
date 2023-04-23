package io.github.fabricators_of_create.porting_lib.registries.mixin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.fabricators_of_create.porting_lib.registries.DynamicRegistryHandler;
import net.minecraft.resources.ResourceLocation;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.resources.RegistryDataLoader;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RegistryDataLoader.class)
public class RegistryDataLoaderMixin {
	@Inject(method = "registryDirPath", at = @At("HEAD"), cancellable = true)
	private static void replaceDynamicRegistryPath(ResourceLocation id, CallbackInfoReturnable<String> cir) {
		if (DynamicRegistryHandler.isModdedRegistryId(id)) {
			cir.setReturnValue(id.getNamespace() + "/" + id.getPath());
		}
	}

	@Shadow
	@Final
	@Mutable
	public static List<RegistryDataLoader.RegistryData<?>> WORLDGEN_REGISTRIES;

	static {
		WORLDGEN_REGISTRIES = new ArrayList<>(WORLDGEN_REGISTRIES);
	}
}
