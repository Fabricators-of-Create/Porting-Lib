package io.github.fabricators_of_create.porting_lib.resources.injections;

import io.github.fabricators_of_create.porting_lib.resources.data_maps.DataMapType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface RegistryInjection<T> {
	@ApiStatus.Internal
	default Map<DataMapType<T, ?>, Map<ResourceKey<T>, ?>> port_lib$getDataMaps() {
		return Map.of();
	}

	/**
	 * {@return the data map value attached with the object with the key, or {@code null} if there's no attached value}
	 *
	 * @param type the type of the data map
	 * @param key  the object to get the value for
	 * @param <A>  the data type
	 */
	@Nullable
	default <A> A getData(DataMapType<T, A> type, ResourceKey<T> key) {
		final var innerMap = port_lib$getDataMaps().get(type);
		return innerMap == null ? null : (A) innerMap.get(key);
	}

	/**
	 * {@return the data map of the given {@code type}}
	 *
	 * @param <A> the data type
	 */
	default <A> Map<ResourceKey<T>, A> getDataMap(DataMapType<T, A> type) {
		return (Map<ResourceKey<T>, A>) port_lib$getDataMaps().getOrDefault(type, Map.of());
	}
}
