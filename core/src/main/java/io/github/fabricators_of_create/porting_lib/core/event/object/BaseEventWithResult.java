package io.github.fabricators_of_create.porting_lib.core.event.object;

import io.github.fabricators_of_create.porting_lib.core.event.EventResult;

/**
 * Base implementation of {@link EventWithResult}.
 */
public class BaseEventWithResult implements EventWithResult {
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
