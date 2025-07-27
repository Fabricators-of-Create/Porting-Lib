package io.github.fabricators_of_create.porting_lib.resources.mixin.common;

import io.github.fabricators_of_create.porting_lib.resources.data_maps.DataMapType;
import io.github.fabricators_of_create.porting_lib.resources.injections.RegistryInjection;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.MappedRegistry;

import net.minecraft.resources.ResourceKey;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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

	@Mixin(targets = "net/minecraft/core/MappedRegistry$1")
	public abstract static class MappedRegistryLookupMixin<T> implements HolderLookup.RegistryLookup<T> {
		@Shadow
		@Final
		private MappedRegistry<T> field_36468;

		@Override
		@Nullable
		public <A> A getData(DataMapType<T, A> type, ResourceKey<T> key) {
			return field_36468.getData(type, key);
		}
	}
}
