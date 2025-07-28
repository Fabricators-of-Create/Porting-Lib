package io.github.fabricators_of_create.porting_lib.registry;

import io.github.fabricators_of_create.porting_lib.registry.injections.HolderInjection;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import org.jetbrains.annotations.Nullable;

public interface DelegatedHolder<T> extends HolderInjection<T> {
	/**
	 * {@return the holder that this holder wraps}
	 *
	 * Used by {@link Registry#safeCastToReference} to resolve the underlying {@link Holder.Reference} for delegating holders.
	 */
	default Holder<T> getDelegate() {
		return (Holder<T>) this;
	}

	static <T> Holder<T> getDelegate(Holder<T> holder) {
		if (holder instanceof DelegatedHolder) {
			return ((DelegatedHolder<T>) holder).getDelegate();
		}

		return holder;
	}

	/**
	 * Attempts to resolve the underlying {@link HolderLookup.RegistryLookup} from a {@link Holder}.
	 * <p>
	 * This will only succeed if the underlying holder is a {@link Holder.Reference}.
	 */
	@Nullable
	default HolderLookup.RegistryLookup<T> unwrapLookup() {
		return null;
	}

	/**
	 * Get the resource key held by this Holder, or null if none is present. This method will be overriden
	 * by Holder implementations to avoid allocation associated with {@link Holder#unwrapKey()}
	 */
	@Nullable
	default ResourceKey<T> getKey() {
		return ((Holder<T>) this).unwrapKey().orElse(null);
	}

	@Override
	default HolderLookup.@Nullable RegistryLookup<T> port_lib$unwrapLookup() {
		return unwrapLookup();
	}

	@Override
	@Nullable
	default ResourceKey<T> port_lib$getKey() {
		return getKey();
	}
}
