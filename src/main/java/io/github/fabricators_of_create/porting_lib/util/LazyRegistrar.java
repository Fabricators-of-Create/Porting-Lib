package io.github.fabricators_of_create.porting_lib.util;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LazyRegistrar<T> {
	public final String mod_id;
	private final Registry<T> registry;
	private final Map<RegistryObject<T>, Supplier<? extends T>> entries = new LinkedHashMap<>();
	private final Set<RegistryObject<T>> entriesView = Collections.unmodifiableSet(entries.keySet());

	LazyRegistrar(Registry<T> registry, String modid) {
		this.registry = registry;
		this.mod_id = modid;
	}

//    public <U extends T> RegistryObject<U> register(String id, Supplier<U> entry) {
//        return () -> Registry.register(registry, new ResourceLocation(mod_id, id), entry.get());
//    }

	public static <R> LazyRegistrar<R> create(Registry<R> registry, String id) {
		return new LazyRegistrar<>(registry, id);
	}

	public <R extends T> RegistryObject<R> register(String id, Supplier<R> entry) {
		return register(new ResourceLocation(mod_id, id), entry);
	}

	public <R extends T> RegistryObject<R> register(ResourceLocation id, Supplier<R> entry) {
		RegistryObject<R> obj = new RegistryObject<>(id, entry, ResourceKey.create(registry.key(), id));
		if (entries.putIfAbsent((RegistryObject<T>) obj, entry) != null) {
			throw new IllegalArgumentException("Duplicate registration " + id);
		}
		return obj;
	}

	public void register() {
		entries.forEach((entry, sup) -> {
			Registry.register(registry, entry.getId(), entry.get());
		});
		entries.forEach((entry, sup) -> entry.setWrappedEntry(() -> registry.get(entry.getId())));
	}

	public <B extends Block> RegistryObject<T> register(String name, T b) {
		return register(name, () -> b);
	}

	public Collection<RegistryObject<T>> getEntries() {
		return entriesView;
	}
}
