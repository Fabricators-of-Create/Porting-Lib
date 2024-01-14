package io.github.fabricators_of_create.porting_lib.registries;

import io.github.fabricators_of_create.porting_lib.registries.mixin.NetworkedRegistryDataAccessor;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resources.RegistryDataLoader;

public interface RegistryEvents {

	Event<NewDataPackRegistryCallback> NEW_DATAPACK_REGISTRY = EventFactory.createArrayBacked(NewDataPackRegistryCallback.class, callbacks -> (registry) -> {
		for (NewDataPackRegistryCallback e : callbacks)
			e.onRegisterNewDataPackRegistry(registry);
	});

	interface NewDataPackRegistryCallback {
		void onRegisterNewDataPackRegistry(NewDatapackRegistry registry);
	}

	class NewDatapackRegistry {
		public <T> void register(RegistryDataLoader.RegistryData<T> registryData) {
			register(registryData, null);
		}

		public <T> void register(RegistryDataLoader.RegistryData<T> registryData, @Nullable Codec<T> networkCodec) {
			DynamicRegistryHandler.REGISTRIES.add(new RegistryDataWithNetworkCodec<>(registryData, networkCodec));
			if (networkCodec != null) {
				DynamicRegistryHandler.NETWORKABLE_REGISTRIES.put(registryData.key(), NetworkedRegistryDataAccessor.createNetworkedRegistryData(registryData.key(), networkCodec));
			}
		}
	}

	record RegistryDataWithNetworkCodec<T>(RegistryDataLoader.RegistryData<T> registryData, @Nullable Codec<T> networkCodec) {}
}
