package io.github.fabricators_of_create.porting_lib.util;

import java.util.function.Supplier;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.Nullable;

@SuppressWarnings("CanBeRecord")
public final class RegistryObject<T> implements Supplier<T> {

	private final ResourceLocation id;
	private Supplier<T> wrappedEntry;
	@Nullable
	private final ResourceKey<T> key;

	public RegistryObject(ResourceLocation id, Supplier<T> wrappedEntry, ResourceKey<?> key) {
		this.id = id;
		this.wrappedEntry = wrappedEntry;
		this.key = (ResourceKey<T>) key;
	}

	public ResourceLocation getId() {
		return id;
	}

	public void setWrappedEntry(Supplier<?> wrappedEntry) {
		this.wrappedEntry = (Supplier<T>) wrappedEntry;
	}

	@Override
	public T get() {
		return wrappedEntry.get();
	}

	@Nullable
	public ResourceKey<T> getKey() {
		return this.key;
	}
}
