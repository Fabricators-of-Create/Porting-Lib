package io.github.fabricators_of_create.porting_lib.data;

import com.mojang.serialization.Lifecycle;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;

import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;
import java.util.stream.Stream;

public class PortingLibDataHack {
	@ApiStatus.Internal
	public static boolean inPatch = false;

	public static String prefixNamespace(ResourceLocation registryKey) {
		return registryKey.getNamespace().equals("minecraft") ? registryKey.getPath() : registryKey.getNamespace() + "/" + registryKey.getPath();
	}

	public static class DummyRegistryLookup<T> implements HolderLookup.RegistryLookup<T> {

		private final HolderLookup.RegistryLookup<T> registrylookup;

		public DummyRegistryLookup(HolderLookup.RegistryLookup<T> registrylookup) {
			this.registrylookup = registrylookup;
		}

		@Override
		public ResourceKey<? extends Registry<T>> key() {
			return null;
		}

		@Override
		public Lifecycle registryLifecycle() {
			return this.registrylookup.registryLifecycle();
		}

		@Override
		public Stream<Holder.Reference<T>> listElements() {
			return Stream.empty();
		}

		@Override
		public Stream<HolderSet.Named<T>> listTags() {
			return Stream.empty();
		}

		@Override
		public Optional<Holder.Reference<T>> get(ResourceKey resourceKey) {
			return Optional.empty();
		}

		@Override
		public Optional<HolderSet.Named<T>> get(TagKey tagKey) {
			return Optional.empty();
		}
	}
}
