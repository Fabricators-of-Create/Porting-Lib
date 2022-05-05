package io.github.fabricators_of_create.porting_lib.event.common;

import java.util.List;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.packs.resources.PreparableReloadListener;

@Deprecated(forRemoval = true)
public interface AddReloadListenersCallback {
	Event<AddReloadListenersCallback> EVENT = EventFactory.createArrayBacked(AddReloadListenersCallback.class, callbacks -> listeners -> {
		for (AddReloadListenersCallback callback : callbacks) {
			callback.addReloadListeners(listeners);
		}
	});

	void addReloadListeners(List<PreparableReloadListener> listeners);
}
