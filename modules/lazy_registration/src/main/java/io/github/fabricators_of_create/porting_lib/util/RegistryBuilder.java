package io.github.fabricators_of_create.porting_lib.util;

import com.mojang.serialization.Lifecycle;

import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.DefaultedMappedRegistry;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class RegistryBuilder<T> {
	private final ResourceKey<? extends Registry<T>> registryKey;
//	private final List<RegistryCallback<T>> callbacks = new ArrayList<>();
	@Nullable
	private ResourceLocation defaultKey;
	private int maxId = -1;
	private boolean sync = false;

	public RegistryBuilder(ResourceKey<? extends Registry<T>> registryKey) {
		this.registryKey = registryKey;
	}

	public RegistryBuilder<T> defaultKey(ResourceLocation key) {
		this.defaultKey = key;
		return this;
	}

	public RegistryBuilder<T> defaultKey(ResourceKey<T> key) {
		this.defaultKey = key.location();
		return this;
	}

//	public RegistryBuilder<T> callback(RegistryCallback<T> callback) { TODO: PORT
//		this.callbacks.add(callback);
//		return this;
//	}
//
//	public RegistryBuilder<T> onAdd(AddCallback<T> callback) {
//		return this.callback(callback);
//	}
//
//	public RegistryBuilder<T> onBake(BakeCallback<T> callback) {
//		return this.callback(callback);
//	}
//
//	public RegistryBuilder<T> onClear(ClearCallback<T> callback) {
//		return this.callback(callback);
//	}

	/**
	 * Sets the highest numerical id that an entry in this registry
	 * is <i>allowed</i> to use.
	 * Must be greater than or equal to zero.
	 *
	 * @param maxId the highest numerical id
	 */
	public RegistryBuilder<T> maxId(int maxId) {
		if (maxId < 0)
			throw new IllegalArgumentException("maxId must be greater than or equal to zero");
		this.maxId = maxId;
		return this;
	}

	/**
	 * Sets whether this registry should have its numerical IDs synced to clients.
	 * Default: {@code false}.
	 */
	public RegistryBuilder<T> sync(boolean sync) {
		this.sync = sync;
		return this;
	}

	/**
	 * Creates a new registry from this builder.
	 * Also use {@link DeferredRegister#makeRegistry(Consumer)}
	 * to not have to call this manually.
	 *
	 * @return the created registry
	 */
	public Registry<T> create() {
		FabricRegistryBuilder<T, MappedRegistry<T>> registry = FabricRegistryBuilder.from(this.defaultKey != null
				? new DefaultedMappedRegistry<>(this.defaultKey.toString(), this.registryKey, Lifecycle.stable(), false)
				: new MappedRegistry<>(this.registryKey, Lifecycle.stable(), false));
//		this.callbacks.forEach(registry::addCallback); TODO: PORT not sure if this is even used but i'll just leave a todo here incase
//		if (this.maxId != -1)
//			registry.setMaxId(this.maxId);
		registry.attribute(this.sync ? RegistryAttribute.SYNCED : RegistryAttribute.MODDED);

//		if (this.registrationCheck) { Fabric doesn't need this
//			RegistryManager.trackModdedRegistry(registry.key().location());
//		}

		return registry.buildAndRegister();
	}
}
