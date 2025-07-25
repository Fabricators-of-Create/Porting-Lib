package io.github.fabricators_of_create.porting_lib.resources.mixin.common;

import io.github.fabricators_of_create.porting_lib.resources.conditions.ICondition;
import io.github.fabricators_of_create.porting_lib.resources.extensions.ContextAwareReloadListenerExtension;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(SimplePreparableReloadListener.class)
public abstract class SimplePreparableReloadListenerMixin implements ContextAwareReloadListenerExtension {
	@Unique private ICondition.IContext port_lib$context;
	@Unique private HolderLookup.Provider port_lib$registryLookup;

	@Override
	public void injectContext(ICondition.IContext context, HolderLookup.Provider registryLookup) {
		this.port_lib$context = context;
		this.port_lib$registryLookup = registryLookup;
	}

	@Override
	public ICondition.IContext port_lib$getContext() {
		return this.port_lib$context;
	}

	@Override
	public HolderLookup.Provider getRegistryLookup() {
		return this.port_lib$registryLookup;
	}
}
