package io.github.fabricators_of_create.porting_lib.registry.injections;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.Nullable;

public interface RegistryInjection<T> {
	/**
	 * {@return the key of the element, or null if it is not present in this registry}
	 *
	 * @apiNote This method is different from {@link Registry#getKey(Object)} as it does not return the default key for
	 *          {@link net.minecraft.core.DefaultedRegistry defaulted registries}
	 */
	@Nullable
	default ResourceLocation port_lib$getKeyOrNull(T element) {
		//Note: We override the cases when getKey would return the default rather than just going via getResourceKey to find it
		return ((Registry<T>) this).getKey(element);
	}
}
