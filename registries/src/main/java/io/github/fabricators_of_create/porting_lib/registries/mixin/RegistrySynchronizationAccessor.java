package io.github.fabricators_of_create.porting_lib.registries.mixin;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.resources.ResourceKey;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(RegistrySynchronization.class)
public interface RegistrySynchronizationAccessor {
	@Accessor
	static Map<ResourceKey<? extends Registry<?>>, RegistrySynchronization.NetworkedRegistryData<?>> getNETWORKABLE_REGISTRIES() {
		throw new UnsupportedOperationException();
	}

	@Mutable
	@Accessor
	static void setNETWORKABLE_REGISTRIES(Map<ResourceKey<? extends Registry<?>>, RegistrySynchronization.NetworkedRegistryData<?>> NETWORKABLE_REGISTRIES) {
		throw new UnsupportedOperationException();
	}

	@Invoker
	static <E> void callPut(ImmutableMap.Builder<ResourceKey<? extends Registry<?>>, RegistrySynchronization.NetworkedRegistryData<?>> builder, ResourceKey<? extends Registry<E>> registryKey, Codec<E> codec) {
		throw new UnsupportedOperationException();
	}
}
