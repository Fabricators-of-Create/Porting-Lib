package io.github.fabricators_of_create.porting_lib.registries.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.google.common.collect.ImmutableMap;

import io.github.fabricators_of_create.porting_lib.registries.DynamicRegistryHandler;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.resources.ResourceKey;

@Mixin(RegistrySynchronization.class)
public abstract class RegistrySynchronizationMixin {

	@Inject(method = "method_45958", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/RegistrySynchronization;put(Lcom/google/common/collect/ImmutableMap$Builder;Lnet/minecraft/resources/ResourceKey;Lcom/mojang/serialization/Codec;)V", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
	private static void port_lib$addNetworkCodec(CallbackInfoReturnable<ImmutableMap<ResourceKey<? extends Registry<?>>, RegistrySynchronization.NetworkedRegistryData<?>>> cir, ImmutableMap.Builder<ResourceKey<? extends Registry<?>>, RegistrySynchronization.NetworkedRegistryData<?>> builder) {
		DynamicRegistryHandler.loadDynamicRegistries();
		DynamicRegistryHandler.NETWORKABLE_REGISTRIES.forEach(builder::put);
	}
}
