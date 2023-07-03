package io.github.fabricators_of_create.porting_lib.core.event.object;

import io.github.fabricators_of_create.porting_lib.core.event.CancelBypass;
import io.github.fabricators_of_create.porting_lib.core.event.CancelBypass.EventHolder;
import net.fabricmc.fabric.api.event.Event;

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

	public <T> boolean shouldInvokeListener(EventHolder<T> event, T listener) {
		return !isCancelled() || CancelBypass.ignoresCancellation(event.get(), listener);
	}

	// forge uses 1 L. both spellings are valid, but I prefer 2, so here's some bridges.
	public void setCanceled(boolean cancelled) { setCancelled(cancelled); }
	public boolean isCanceled() { return isCancelled(); }
}
