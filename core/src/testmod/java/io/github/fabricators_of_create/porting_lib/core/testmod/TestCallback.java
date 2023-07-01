package io.github.fabricators_of_create.porting_lib.core.testmod;

import io.github.fabricators_of_create.porting_lib.core.event.object.CancellableEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface TestCallback {
	Event<TestCallback> EVENT = EventFactory.createArrayBacked(TestCallback.class, callbacks -> event -> {
		for (TestCallback callback : callbacks) {
			if (event.shouldInvokeListener(getEvent(), callback))
				callback.doTest(event);
		}
	});

	void doTest(TestEvent event);

	static Event<TestCallback> getEvent() { return EVENT; }

	class TestEvent extends CancellableEvent {
	}
}
