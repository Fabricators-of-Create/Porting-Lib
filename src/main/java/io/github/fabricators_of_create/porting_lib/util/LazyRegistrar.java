package io.github.fabricators_of_create.porting_lib.util;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class LazyRegistrar<T> {
	public final String mod_id;
	private final Registry<T> registry;
	private final List<RegistryObject<? extends T>> entires;

	LazyRegistrar(Registry<T> registry, String modid) {
		this.registry = registry;
		this.mod_id = modid;
		this.entires = new ArrayList<>();
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
		RegistryObject<R> obj = new RegistryObject<>(id, entry);
		entires.add(obj);
		return obj;
	}

	public void register() {
		entires.forEach(entry -> Registry.register(registry, entry.getId(), entry.get()));
		entires.forEach(entry -> entry.setWrappedEntry(() -> registry.get(entry.getId())));
	}

	public <B extends Block> RegistryObject register(String name, T b) {
		return register(name, () -> b);
	}
}
