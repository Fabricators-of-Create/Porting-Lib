package io.github.fabricators_of_create.porting_lib.util;

import java.util.Optional;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.common.base.Suppliers;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("CanBeRecord")
public final class RegistryObject<T> implements Supplier<T> {

	private final ResourceLocation id;
	private Supplier<T> wrappedEntry;
	@Nullable
	private final ResourceKey<T> key;

	@Nullable
	private Holder<T> holder;

	public RegistryObject(ResourceLocation id, Supplier<T> wrappedEntry, ResourceKey<?> key) {
		this.id = id;
		this.wrappedEntry = Suppliers.memoize(wrappedEntry::get);
		this.key = (ResourceKey<T>) key;
	}

	public ResourceLocation getId() {
		return id;
	}

	public void setWrappedEntry(Supplier<?> wrappedEntry) {
		this.wrappedEntry = () -> (T) Suppliers.memoize(wrappedEntry::get).get();
	}

	@Override
	public T get() {
		return wrappedEntry.get();
	}

	@Nullable
	public ResourceKey<T> getKey() {
		return this.key;
	}

	@SuppressWarnings("unchecked")
	@NotNull
	public Optional<Holder<T>> getHolder() {
		if (this.holder == null && this.key != null && registryExists(this.key.registry())) {
			ResourceLocation registryName = this.key.registry();
			Registry<T> registry = (Registry<T>) BuiltInRegistries.REGISTRY.get(registryName);

			if (registry != null)
				this.holder = registry.getHolder(this.key).orElse(null);
		}

		return Optional.ofNullable(this.holder);
	}

	private static boolean registryExists(ResourceLocation registryName) {
		return BuiltInRegistries.REGISTRY.containsKey(registryName);
	}
}
