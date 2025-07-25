package io.github.fabricators_of_create.porting_lib.resources.extensions;

import io.github.fabricators_of_create.porting_lib.resources.conditions.ICondition;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;

import org.jetbrains.annotations.ApiStatus;

public interface ContextAwareReloadListenerExtension {
	@ApiStatus.Internal
	void injectContext(ICondition.IContext context, HolderLookup.Provider registryLookup);

	/**
	 * Returns the condition context held by this listener, or {@link ICondition.IContext#EMPTY} if it is unavailable.
	 */
	ICondition.IContext port_lib$getContext();

	/**
	 * Returns the registry access held by this listener, or {@link RegistryAccess#EMPTY} if it is unavailable.
	 */
	HolderLookup.Provider getRegistryLookup();
}
