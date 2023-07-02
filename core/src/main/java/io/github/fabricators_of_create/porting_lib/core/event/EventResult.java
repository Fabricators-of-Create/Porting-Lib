package io.github.fabricators_of_create.porting_lib.core.event;

import io.github.fabricators_of_create.porting_lib.core.event.object.EventWithResult;

/**
 * Possible results for an event, usually an {@link EventWithResult}.
 */
public enum EventResult {
	DENY,
	DEFAULT,
	ALLOW
}
