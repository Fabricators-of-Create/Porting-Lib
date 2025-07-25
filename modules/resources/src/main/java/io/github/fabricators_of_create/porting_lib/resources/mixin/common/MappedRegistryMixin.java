package io.github.fabricators_of_create.porting_lib.resources.mixin.common;

import io.github.fabricators_of_create.porting_lib.resources.data_maps.DataMapType;
import io.github.fabricators_of_create.porting_lib.resources.injections.RegistryInjection;
import net.minecraft.core.MappedRegistry;

import net.minecraft.resources.ResourceKey;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.IdentityHashMap;
import java.util.Map;

@Mixin(MappedRegistry.class)
public abstract class MappedRegistryMixin<T> implements RegistryInjection<T> {
	@Unique private final Map<DataMapType<T, ?>, Map<ResourceKey<T>, ?>> port_lib$dataMaps = new IdentityHashMap<>();

	@Override
	public Map<DataMapType<T, ?>, Map<ResourceKey<T>, ?>> port_lib$getDataMaps() {
		return port_lib$dataMaps;
	}
}
