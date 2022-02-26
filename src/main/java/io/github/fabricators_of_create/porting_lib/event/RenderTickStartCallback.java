package io.github.fabricators_of_create.porting_lib.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface RenderTickStartCallback {
	Event<RenderTickStartCallback> EVENT = EventFactory.createArrayBacked(RenderTickStartCallback.class, callbacks -> () -> {
		for (RenderTickStartCallback callback : callbacks) {
			callback.tick();
		}
	});

	void tick();
}
