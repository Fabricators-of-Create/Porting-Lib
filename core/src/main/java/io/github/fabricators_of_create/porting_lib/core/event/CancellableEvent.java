package io.github.fabricators_of_create.porting_lib.core.event;

/**
 * An event that may be cancelled.
 * @see EventWithResult
 * @see CancellableEventWithResult
 */
public interface CancellableEvent {
	void setCancelled();
	boolean isCancelled();

	class Base implements CancellableEvent {
		private boolean cancelled;

		public void setCancelled() {
			cancelled = true;
		}

		public boolean isCancelled() {
			return cancelled;
		}
	}
}
