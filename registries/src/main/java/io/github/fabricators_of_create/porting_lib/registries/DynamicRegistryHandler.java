package io.github.fabricators_of_create.porting_lib.registries;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;

import io.github.fabricators_of_create.porting_lib.registries.mixin.RegistrySynchronizationAccessor;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class DynamicRegistryHandler {
	public static List<ResourceLocation> REGISTRIES = new ArrayList<>();

	private static boolean frozen = false;

	public static void freeze() {
		frozen = true;
	}

	public static boolean isModdedRegistryId(ResourceLocation id) {
		return REGISTRIES.contains(id);
	}

	public static  <T> void register(RegistryDataLoader.RegistryData<T> registryData) {
		if (frozen) throw new IllegalStateException("Registry is already frozen");
		REGISTRIES.add(registryData.key().location());
		RegistryDataLoader.WORLDGEN_REGISTRIES.add(registryData);
	}

	public <T> void register(RegistryDataLoader.RegistryData<T> registryData, @Nullable Codec<T> networkCodec) {
		register(registryData);
		var builder = ImmutableMap.<ResourceKey<? extends Registry<?>>, RegistrySynchronization.NetworkedRegistryData<?>>builder().putAll(RegistrySynchronizationAccessor.getNETWORKABLE_REGISTRIES());
		RegistrySynchronizationAccessor.callPut(builder, registryData.key(), networkCodec);
		RegistrySynchronizationAccessor.setNETWORKABLE_REGISTRIES(builder.build());
	}
}
