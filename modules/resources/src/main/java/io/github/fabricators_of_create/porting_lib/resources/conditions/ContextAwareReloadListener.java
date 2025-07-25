package io.github.fabricators_of_create.porting_lib.resources.conditions;

import com.google.gson.JsonElement;

import com.mojang.serialization.JsonOps;

import io.github.fabricators_of_create.porting_lib.resources.extensions.ContextAwareReloadListenerExtension;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import net.minecraft.server.packs.resources.SimplePreparableReloadListener;

import org.jetbrains.annotations.ApiStatus;

/**
 * Reload listeners that descend from this class will have the reload context automatically populated when it is available.
 * <p>
 * The context is guaranteed to be available for the duration of {@link PreparableReloadListener#reload}.
 * <p>
 * For children of {@link SimplePreparableReloadListener}, it will be available during both {@link SimplePreparableReloadListener#prepare} prepare()} and {@link SimplePreparableReloadListener#apply apply()}.
 */
public abstract class ContextAwareReloadListener implements PreparableReloadListener, ContextAwareReloadListenerExtension {
	private ICondition.IContext conditionContext = ICondition.IContext.EMPTY;

	private HolderLookup.Provider registryLookup = RegistryAccess.EMPTY;

	@ApiStatus.Internal
	public void injectContext(ICondition.IContext context, HolderLookup.Provider registryLookup) {
		this.conditionContext = context;
		this.registryLookup = registryLookup;
	}

	/**
	 * Returns the condition context held by this listener, or {@link ICondition.IContext#EMPTY} if it is unavailable.
	 */
	public final ICondition.IContext port_lib$getContext() {
		return this.conditionContext;
	}

	/**
	 * Returns the registry access held by this listener, or {@link RegistryAccess#EMPTY} if it is unavailable.
	 */
	public final HolderLookup.Provider getRegistryLookup() {
		return this.registryLookup;
	}

	/**
	 * Creates a new {@link ConditionalOps} using {@link #port_lib$getContext()} and {@link #getRegistryLookup()} ()}.
	 */
	protected final ConditionalOps<JsonElement> makeConditionalOps() {
		return new ConditionalOps<>(getRegistryLookup().createSerializationContext(JsonOps.INSTANCE), port_lib$getContext());
	}
}
