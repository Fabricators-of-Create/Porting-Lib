package io.github.fabricators_of_create.porting_lib.resources.data_maps;

import io.github.fabricators_of_create.porting_lib.resources.events.AddReloadListenersEvent;
import io.github.fabricators_of_create.porting_lib.resources.events.TagsUpdatedEvent;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public class PortingLibDataMaps {
	private static final Map<ResourceKey<Registry<?>>, Map<ResourceLocation, DataMapType<?, ?>>> dataMaps = new IdentityHashMap<>();
	private static DataMapLoader DATA_MAPS;

	public static void init() {
		AddReloadListenersEvent.EVENT.register(event -> {
			event.addListener(DATA_MAPS = new DataMapLoader(event.getConditionContext(), event.getRegistryAccess()));
		});

		TagsUpdatedEvent.EVENT.register(event -> {
			if (event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD) {
				DATA_MAPS.apply();
			}
		});
	}

	/**
	 * {@return a view of all registered data maps}
	 */
	public static Map<ResourceKey<Registry<?>>, Map<ResourceLocation, DataMapType<?, ?>>> getDataMaps() {
		return dataMaps;
	}

	@Nullable
	public static <R> DataMapType<R, ?> getDataMap(ResourceKey<? extends Registry<R>> registry, ResourceLocation key) {
		final var map = dataMaps.get(registry);
		return map == null ? null : (DataMapType<R, ?>) map.get(key);
	}

	/**
	 * Register a registry data map.
	 *
	 * @param type the data map type to register
	 * @param <T>  the type of the data map
	 * @param <R>  the type of the registry
	 * @throws IllegalArgumentException      if a type with the same ID has already been registered for that registry
	 * @throws UnsupportedOperationException if the registry is a non-synced datapack registry and the data map is synced
	 */
	public static <T, R> void registerDataMap(DataMapType<R, T> type) {
		final var registryKey = type.registryKey();

		if (DynamicRegistries.getDynamicRegistries().stream().anyMatch(data -> data.key().equals(registryKey))) {
			if (type.networkCodec() != null && RegistryDataLoader.SYNCHRONIZED_REGISTRIES.stream().noneMatch(data -> data.key().equals(registryKey))) {
				throw new UnsupportedOperationException("Cannot register synced data map " + type.id() + " for datapack registry " + registryKey.location() + " that is not synced!");
			}
		}

		final var map = dataMaps.computeIfAbsent((ResourceKey) registryKey, k -> new HashMap<>());
		if (map.containsKey(type.id())) {
			throw new IllegalArgumentException("Tried to register data map type with ID " + type.id() + " to registry " + registryKey.location() + " twice!");
		}

		map.put(type.id(), type);
	}
}
