package io.github.fabricators_of_create.porting_lib.core.event.object;

/**
 * An event that may be cancelled.
 * @see EventWithResult
 * @see CancellableEventWithResult
 */
public class CancellableEvent {
	private boolean cancelled;

	public void setCancelled() {
		cancelled = true;
	}

	public boolean isCancelled() {
		return cancelled;
	}

}
