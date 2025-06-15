package io.github.fabricators_of_create.porting_lib.data.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import io.github.fabricators_of_create.porting_lib.data.PortingLibDataHack;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;

import net.minecraft.resources.RegistryDataLoader;

import org.jetbrains.annotations.Unmodifiable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.stream.Stream;

@Mixin(DynamicRegistries.class)
public class DynamicRegistriesMixin {
	// Based Fabric API mixin (Used by data gen)
	@ModifyReturnValue(method = "getDynamicRegistries", at = @At("RETURN"), remap = false)
	private static List<RegistryDataLoader.RegistryData<?>> addDimensionRegistry(@Unmodifiable List<RegistryDataLoader.RegistryData<?>> original) {
		if (PortingLibDataHack.inPatch) {
			PortingLibDataHack.inPatch = false;
			return Stream.concat(original.stream(), RegistryDataLoader.DIMENSION_REGISTRIES.stream()).toList();
		}
		return original;
	}
}
