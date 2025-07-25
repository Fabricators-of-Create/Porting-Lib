package io.github.fabricators_of_create.porting_lib.resources.events;

import com.google.common.collect.ImmutableList;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;
import io.github.fabricators_of_create.porting_lib.resources.conditions.ContextAwareReloadListener;
import io.github.fabricators_of_create.porting_lib.resources.conditions.ICondition;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

/**
 * The main ResourceManager is recreated on each reload, just after {@link ReloadableServerResources}'s creation.
 *
 * The event is fired on each reload and lets modders add their own ReloadListeners, for server-side resources.
 * The only reason to use this over Fabric API's {@link ResourceManagerHelper#registerReloadListener(IdentifiableResourceReloadListener)} or {@link ResourceManagerHelper#registerReloadListener(ResourceLocation, Function)}
 * is if you require {@link RegistryAccess} specifically.
 */
public class AddReloadListenersEvent extends BaseEvent {
	public static final Event<AddReloadListenersCallback> EVENT = EventFactory.createArrayBacked(AddReloadListenersCallback.class, callbacks -> event -> {
		for (AddReloadListenersCallback callback : callbacks) {
			callback.onAddReloadListeners(event);
		}
	});

	private final List<IdentifiableResourceReloadListener> listeners = new ArrayList<>();
	private final ReloadableServerResources serverResources;
	private final RegistryAccess registryAccess;

	public AddReloadListenersEvent(ReloadableServerResources serverResources, RegistryAccess registryAccess) {
		this.serverResources = serverResources;
		this.registryAccess = registryAccess;
	}

	/**
	 * @param listener the listener to add to the ResourceManager on reload
	 */
	public void addListener(IdentifiableResourceReloadListener listener) {
		listeners.add(new WrappedStateAwareListener(listener));
	}

	public List<IdentifiableResourceReloadListener> getListeners() {
		return ImmutableList.copyOf(listeners);
	}

	/**
	 * @return The ReloableServerResources being reloaded.
	 */
	public ReloadableServerResources getServerResources() {
		return serverResources;
	}

	/**
	 * This context object holds data relevant to the current reload, such as staged tags.
	 *
	 * @return The condition context for the currently active reload.
	 */
	public ICondition.IContext getConditionContext() {
		return serverResources.port_lib$getConditionContext();
	}

	/**
	 * Provides access to the loaded registries associated with these server resources.
	 * All built-in and dynamic registries are loaded and frozen by this point.
	 *
	 * @return The RegistryAccess context for the currently active reload.
	 */
	public RegistryAccess getRegistryAccess() {
		return registryAccess;
	}

	@Override
	public void sendEvent() {
		EVENT.invoker().onAddReloadListeners(this);
	}

	@FunctionalInterface
	public interface AddReloadListenersCallback {
		void onAddReloadListeners(AddReloadListenersEvent event);
	}

	private static class WrappedStateAwareListener extends ContextAwareReloadListener implements IdentifiableResourceReloadListener {
		private final IdentifiableResourceReloadListener wrapped;

		private WrappedStateAwareListener(final IdentifiableResourceReloadListener wrapped) {
			this.wrapped = wrapped;
		}

		@Override
		public ResourceLocation getFabricId() {
			return wrapped.getFabricId();
		}

		@Override
		public Collection<ResourceLocation> getFabricDependencies() {
			return wrapped.getFabricDependencies();
		}

		@Override
		public void injectContext(ICondition.IContext context, HolderLookup.Provider registryLookup) {
			if (this.wrapped instanceof ContextAwareReloadListener contextAwareListener) {
				contextAwareListener.injectContext(context, registryLookup);
			}
		}

		@Override
		public CompletableFuture<Void> reload(final PreparationBarrier stage, final ResourceManager resourceManager, final ProfilerFiller preparationsProfiler, final ProfilerFiller reloadProfiler, final Executor backgroundExecutor, final Executor gameExecutor) {
			return wrapped.reload(stage, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
		}
	}
}
