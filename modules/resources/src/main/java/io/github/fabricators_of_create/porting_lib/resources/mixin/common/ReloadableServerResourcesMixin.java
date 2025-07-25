package io.github.fabricators_of_create.porting_lib.resources.mixin.common;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.resources.conditions.ConditionContext;
import io.github.fabricators_of_create.porting_lib.resources.conditions.ICondition;
import io.github.fabricators_of_create.porting_lib.resources.events.AddReloadListenersEvent;
import io.github.fabricators_of_create.porting_lib.resources.events.TagsUpdatedEvent;
import io.github.fabricators_of_create.porting_lib.resources.extensions.ContextAwareReloadListenerExtension;
import io.github.fabricators_of_create.porting_lib.resources.injections.ReloadableServerResourcesInjection;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.server.ReloadableServerResources;

import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.tags.TagManager;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(ReloadableServerResources.class)
public abstract class ReloadableServerResourcesMixin implements ReloadableServerResourcesInjection {
	@Shadow
	@Final
	private TagManager tagManager;

	@Shadow
	@Final
	private ReloadableServerResources.ConfigurableRegistryLookup registryLookup;

	@Shadow
	@Final
	private ReloadableServerRegistries.Holder fullRegistryHolder;
	@Unique
	private ICondition.IContext port_lib$context;

	@Override
	public ICondition.IContext port_lib$getConditionContext() {
		if (this.port_lib$context == null) // Need to initialize this when it's queried, otherwise tagManager seems to fail.
		 	this.port_lib$context = new ConditionContext(this.tagManager);

		return this.port_lib$context;
	}


	@Override
	public HolderLookup.Provider port_lib$getRegistryLookup() {
		return this.registryLookup;
	}

	@ModifyArg(method = "method_58296", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/resources/SimpleReloadInstance;create(Lnet/minecraft/server/packs/resources/ResourceManager;Ljava/util/List;Ljava/util/concurrent/Executor;Ljava/util/concurrent/Executor;Ljava/util/concurrent/CompletableFuture;Z)Lnet/minecraft/server/packs/resources/ReloadInstance;"))
	private static List<PreparableReloadListener> port_lib$invokeOnResourceReload(List<PreparableReloadListener> listeners, @Local ReloadableServerResources resources, @Local(argsOnly = true) LayeredRegistryAccess<RegistryLayer> registryAccess) {
		List<PreparableReloadListener> newListeners = new ArrayList<>(listeners);
		AddReloadListenersEvent event = new AddReloadListenersEvent(resources, registryAccess.compositeAccess());
		event.sendEvent();

		newListeners.addAll(event.getListeners());

		for (PreparableReloadListener listener : newListeners) {
			if (listener instanceof ContextAwareReloadListenerExtension contextAwareReloadListener) {
				contextAwareReloadListener.injectContext(resources.port_lib$getConditionContext(), resources.port_lib$getRegistryLookup());
			}
		}

		return newListeners;
	}

	@Inject(method = "updateRegistryTags()V", at = @At("TAIL"))
	public void port_lib$updateTags(CallbackInfo ci) {
		new TagsUpdatedEvent(this.fullRegistryHolder.get(), false, false).sendEvent();
	}
}
