package io.github.fabricators_of_create.porting_lib.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public class LazyRegistrar<T> {
	public final String mod_id;
	private final RegistryKey<? extends Registry<T>> registryKey;
	private final Map<RegistryObject<T>, Supplier<? extends T>> entries = new LinkedHashMap<>();
	private final Set<RegistryObject<T>> entriesView = Collections.unmodifiableSet(entries.keySet());

	LazyRegistrar(RegistryKey<? extends Registry<T>> registryKey, String modid) {
		this.registryKey = registryKey;
		this.mod_id = modid;
	}

//    public <U extends T> RegistryObject<U> register(String id, Supplier<U> entry) {
//        return () -> Registry.register(registry, new ResourceLocation(mod_id, id), entry.get());
//    }

	public static <R> LazyRegistrar<R> create(Registry<R> registry, String id) {
		return new LazyRegistrar<>(registry.getKey(), id);
	}

	public static <B> LazyRegistrar<B> create(RegistryKey<? extends Registry<B>> registry, String id) {
		return new LazyRegistrar<>(registry, id);
	}

	public static <B> LazyRegistrar<B> create(Identifier registryName, String id) {
		return new LazyRegistrar<>(RegistryKey.ofRegistry(registryName), id);
	}

	private Supplier<Registry<T>> cachedHolder;

	public Supplier<Registry<T>> makeRegistry() {
		if (cachedHolder == null)
			cachedHolder = new RegistryHolder<>(getRegistryKey());
		return cachedHolder;
	}

	public <R extends T> RegistryObject<R> register(String id, Supplier<? extends R> entry) {
		return register(new Identifier(mod_id, id), entry);
	}

	public <R extends T> RegistryObject<R> register(Identifier id, final Supplier<? extends R> entry) {
		RegistryObject<? extends R> obj = new RegistryObject<>(id, entry, RegistryKey.of(getRegistryKey(), id));
		if (entries.putIfAbsent((RegistryObject<T>) obj, entry) != null) {
			throw new IllegalArgumentException("Duplicate registration " + id);
		}
		return (RegistryObject<R>) obj;
	}

	public void register() {
		Registry<T> registry = makeRegistry().get();
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

	/**
	 * @return The registry key stored in this deferred register. Useful for creating new deferred registers based on an existing one.
	 */
	public RegistryKey<? extends Registry<T>> getRegistryKey() {
		return this.registryKey;
	}

	private static class RegistryHolder<V> implements Supplier<Registry<V>> {
		private final RegistryKey<? extends Registry<V>> registryKey;
		private Registry<V> registry = null;
		private final List<Registry<? extends Registry<?>>> registries;

		private RegistryHolder(RegistryKey<? extends Registry<V>> registryKey) {
			this.registryKey = registryKey;
			this.registries = new ArrayList<>();
			registerRegistry(Registry.REGISTRIES);
			registerRegistry(BuiltinRegistries.REGISTRIES);
		}

		public void registerRegistry(Registry<? extends Registry<?>> registry) {
			registries.add(registry);
		}

		@Override
		public Registry<V> get() {
			// Keep looking up the registry until it's not null
			registries.forEach(reg -> {
				if (reg.containsId(registryKey.getValue()))
					this.registry = (Registry<V>) reg.get(registryKey.getValue());
			});
			if (this.registry == null)
				this.registry = (Registry<V>) FabricRegistryBuilder.createSimple(null, registryKey.getValue()).buildAndRegister();

			return this.registry;
		}
	}
}
