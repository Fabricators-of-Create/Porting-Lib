package io.github.fabricators_of_create.porting_lib.core.event.object;

import io.github.fabricators_of_create.porting_lib.core.event.EventResult;

/**
 * An event with a {@link EventResult}
 * @see BaseEventWithResult
 * @see CancellableEvent
 * @see CancellableEventWithResult
 */
public interface EventWithResult {
	EventResult getResult();

	void setResult(EventResult result);
}
