package io.github.fabricators_of_create.porting_lib.core.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * This event is fired after the {@link net.fabricmc.api.ModInitializer} entrypoint
 */
public interface ModsLoadedCallback {
	Event<ModsLoadedCallback> EVENT = EventFactory.createArrayBacked(ModsLoadedCallback.class, callbacks -> envType -> {
		for (ModsLoadedCallback event : callbacks)
			event.onAllModsLoaded(envType);
	});

	void onAllModsLoaded(EnvType envType);
}
