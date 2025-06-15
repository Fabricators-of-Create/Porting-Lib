package io.github.fabricators_of_create.porting_lib.data.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import com.llamalad7.mixinextras.sugar.Cancellable;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.data.PortingLibDataHack;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;

import net.minecraft.resources.ResourceKey;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RegistrySetBuilder.class)
public class RegistrySetBuilderMixin {
	@WrapOperation(
			method = "createLazyFullPatchedRegistries(Lnet/minecraft/core/HolderOwner;Lnet/minecraft/core/Cloner$Factory;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/core/HolderLookup$Provider;Lnet/minecraft/core/HolderLookup$Provider;Lorg/apache/commons/lang3/mutable/MutableObject;)Lnet/minecraft/core/HolderLookup$RegistryLookup;",
			at = @At(
					value = "INVOKE", target = "Lnet/minecraft/core/HolderLookup$Provider;lookupOrThrow(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/core/HolderLookup$RegistryLookup;",
					ordinal = 1
			)
	)
	private <T> HolderLookup.RegistryLookup<T> bypassException(HolderLookup.Provider instance, ResourceKey<? extends Registry<? extends T>> registryKey, Operation<HolderLookup.RegistryLookup<T>> original, @Local(ordinal = 0) HolderLookup.RegistryLookup<T> registrylookup) {
		try {
			return original.call(instance, registryKey);
		} catch (Throwable ex) {
			return new PortingLibDataHack.DummyRegistryLookup<>(registrylookup);
		}
	}
}
