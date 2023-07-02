package io.github.fabricators_of_create.porting_lib.core.event.object;

import io.github.fabricators_of_create.porting_lib.core.event.EventResult;

/**
 * An event that is both cancellable and has a result.
 * @see CancellableEvent
 * @see EventWithResult
 */
public class CancellableEventWithResult extends CancellableEvent implements EventWithResult {
	private EventResult result = EventResult.DEFAULT;

	@Override
	public EventResult getResult() {
		return result;
	}

	@Override
	public void setResult(EventResult result) {
		this.result = result;
	}
}
