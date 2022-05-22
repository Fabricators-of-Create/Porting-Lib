package io.github.fabricators_of_create.porting_lib.event.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface PickBlockCallback {
	Event<PickBlockCallback> EVENT = EventFactory.createArrayBacked(PickBlockCallback.class, callbacks -> () -> {
		for (PickBlockCallback callback : callbacks) {
			if (callback.onPickBlock()) {
				return true;
			}
		}
		return false;
	});

	/**
	 * @return true to cancel pick
	 */
	boolean onPickBlock();
}
