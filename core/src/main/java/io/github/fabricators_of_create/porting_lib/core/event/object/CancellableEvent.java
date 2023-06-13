package io.github.fabricators_of_create.porting_lib.core.event.object;

import io.github.fabricators_of_create.porting_lib.core.event.CancelBypass;

/**
 * An event that may be cancelled.
 * @see EventWithResult
 * @see CancellableEventWithResult
 */
public class CancellableEvent {
	private boolean cancelled;

	public void cancel() {
		setCancelled(true);
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	/**
	 * Shortcut to {@link #shouldInvokeListener(Class, String, Object)} where the field name is assumed to be 'EVENT'.
	 * @param callbackClass the class holding the associated event
	 */
	public <T> boolean shouldInvokeListener(Class<T> callbackClass, T listener) {
		return shouldInvokeListener(callbackClass, "EVENT", listener);
	}

	/**
	 *
	 * @param callbackClass the class holding the associated event
	 * @param eventField the field name of the associated event
	 */
	public <T> boolean shouldInvokeListener(Class<T> callbackClass, String eventField, T listener) {
		return !isCancelled() || CancelBypass.ignoresCancellation(callbackClass, eventField, listener);
	}

	// forge uses 1 L. both spellings are valid, but I prefer 2, so here's some bridges.
	public void setCanceled(boolean cancelled) { setCancelled(cancelled); }
	public boolean isCanceled() { return isCancelled(); }
}
