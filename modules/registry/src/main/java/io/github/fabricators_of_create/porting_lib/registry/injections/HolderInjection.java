package io.github.fabricators_of_create.porting_lib.registry.injections;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;

import org.jetbrains.annotations.Nullable;

public interface HolderInjection<T> {
	/**
	 * Attempts to resolve the underlying {@link HolderLookup.RegistryLookup} from a {@link Holder}.
	 * <p>
	 * This will only succeed if the underlying holder is a {@link Holder.Reference}.
	 */
	@Nullable
	default HolderLookup.RegistryLookup<T> port_lib$unwrapLookup() {
		return null;
	}

	/**
	 * Get the resource key held by this Holder, or null if none is present. This method will be overriden
	 * by Holder implementations to avoid allocation associated with {@link Holder#unwrapKey()}
	 */
	@Nullable
	default ResourceKey<T> port_lib$getKey() {
		return ((Holder<T>) this).unwrapKey().orElse(null);
	}
}
