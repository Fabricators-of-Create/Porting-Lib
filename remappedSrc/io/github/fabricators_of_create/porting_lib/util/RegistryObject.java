package io.github.fabricators_of_create.porting_lib.util;

import java.util.Optional;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;

import net.minecraft.core.Holder;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("CanBeRecord")
public final class RegistryObject<T> implements Supplier<T> {

	private final Identifier id;
	private Supplier<T> wrappedEntry;
	@Nullable
	private final RegistryKey<T> key;

	@Nullable
	private Holder<T> holder;

	public RegistryObject(Identifier id, Supplier<T> wrappedEntry, RegistryKey<?> key) {
		this.id = id;
		this.wrappedEntry = Suppliers.memoize(wrappedEntry::get);
		this.key = (RegistryKey<T>) key;
	}

	public Identifier getId() {
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
	public RegistryKey<T> getKey() {
		return this.key;
	}

	@SuppressWarnings("unchecked")
	@NotNull
	public Optional<Holder<T>> getHolder() {
		if (this.holder == null && this.key != null && registryExists(this.key.registry())) {
			Identifier registryName = this.key.registry();
			Registry<T> registry = (Registry<T>) Registry.REGISTRIES.get(registryName);
			if (registry == null)
				registry = (Registry<T>) BuiltinRegistries.REGISTRIES.get(registryName);

			if (registry != null)
				this.holder = registry.getOrCreateHolder(this.key);
		}

		return Optional.ofNullable(this.holder);
	}

	private static boolean registryExists(Identifier registryName) {
		return Registry.REGISTRIES.containsId(registryName)
				|| BuiltinRegistries.REGISTRIES.containsId(registryName);
	}
}
