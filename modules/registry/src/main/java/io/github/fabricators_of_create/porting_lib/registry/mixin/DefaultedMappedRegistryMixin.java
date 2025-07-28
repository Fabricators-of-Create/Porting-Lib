package io.github.fabricators_of_create.porting_lib.registry.mixin;

import com.mojang.serialization.Lifecycle;

import net.minecraft.core.DefaultedMappedRegistry;

import net.minecraft.core.MappedRegistry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DefaultedMappedRegistry.class)
public abstract class DefaultedMappedRegistryMixin<T> extends MappedRegistry<T> {
	public DefaultedMappedRegistryMixin(ResourceKey<? extends Registry<T>> key, Lifecycle registryLifecycle) {
		super(key, registryLifecycle);
	}

	@Nullable
	@Override
	public ResourceLocation port_lib$getKeyOrNull(T element) {
		return super.getKey(element);
	}
}
