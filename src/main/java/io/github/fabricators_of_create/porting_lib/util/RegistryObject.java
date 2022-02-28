package io.github.fabricators_of_create.porting_lib.util;

import java.util.function.Supplier;

import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("CanBeRecord")
public final class RegistryObject<T> implements Supplier<T> {

	private final ResourceLocation id;
	private Supplier<T> wrappedEntry;

	public RegistryObject(ResourceLocation id, Supplier<T> wrappedEntry) {
		this.id = id;
		this.wrappedEntry = wrappedEntry;
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
}
