package io.github.fabricators_of_create.porting_lib.registries;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.resources.RegistryDataLoader;

import net.minecraft.resources.ResourceKey;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class DynamicRegistryHandler {
	public static List<RegistryEvents.RegistryDataWithNetworkCodec<?>> REGISTRIES = new ArrayList<>();
	public static final Map<ResourceKey<? extends Registry<?>>, RegistrySynchronization.NetworkedRegistryData<?>> NETWORKABLE_REGISTRIES = new LinkedHashMap<>();

	public static List<RegistryDataLoader.RegistryData<?>> getRegistryData() {
		return DynamicRegistryHandler.REGISTRIES.stream().map(RegistryEvents.RegistryDataWithNetworkCodec::registryData).collect(Collectors.toList());
	}

	private static boolean init = false;

	public static void loadDynamicRegistries() {
		if (!init) {
			RegistryEvents.NEW_DATAPACK_REGISTRY.invoker().onRegisterNewDataPackRegistry(new RegistryEvents.NewDatapackRegistry());
			init = true;
		}
	}
}
