package io.github.fabricators_of_create.porting_lib.entity;

/**
 * The base of an event that may be cancelled.
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
