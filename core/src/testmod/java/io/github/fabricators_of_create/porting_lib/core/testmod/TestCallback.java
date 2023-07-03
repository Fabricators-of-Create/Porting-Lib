package io.github.fabricators_of_create.porting_lib.core.testmod;

import io.github.fabricators_of_create.porting_lib.core.event.CancelBypass;
import io.github.fabricators_of_create.porting_lib.core.event.object.CancellableEvent;
import net.fabricmc.fabric.api.event.Event;

public interface TestCallback {
	Event<TestCallback> EVENT = CancelBypass.makeEvent(TestCallback.class, eventHolder -> callbacks -> event -> {
		for (TestCallback callback : callbacks) {
			if (event.shouldInvokeListener(eventHolder, callback))
				callback.doTest(event);
		}
	});

	void doTest(TestEvent event);

	class TestEvent extends CancellableEvent {
	}
}
