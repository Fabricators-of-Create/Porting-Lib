package io.github.fabricators_of_create.porting_lib.resources.injections;

import io.github.fabricators_of_create.porting_lib.resources.data_maps.DataMapType;
import net.minecraft.resources.ResourceKey;

import org.jetbrains.annotations.Nullable;

public interface RegistryLookupInjection<T> {
	@Nullable
	default <A> A getData(DataMapType<T, A> attachment, ResourceKey<T> key) {
		return null;
	}
}
