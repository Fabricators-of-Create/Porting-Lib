package io.github.fabricators_of_create.porting_lib.core.event;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

/**
 * Interface for events that can be canceled.
 * Listeners registered to the event bus will not receive {@link #isCanceled() canceled} events,
 * unless they were registered with {@code receiveCanceled = true}.
 */
public interface CancellableEvent {
	/**
	 * Sets the cancel state of this event.
	 *
	 * <p>This will prevent other listeners from receiving this event unless they were registered with
	 * {@code receiveCanceled = true}.
	 * Further effects of setting the cancel state are defined on a per-event basis.
	 *
	 * <p>This method may be overridden to react to cancellation of the event,
	 * however a super call must always be made as follows:
	 * {@code ICancellableEvent.super.setCanceled(canceled);}
	 */
	@MustBeInvokedByOverriders
	default void setCanceled(boolean canceled) {
		((BaseEvent) this).isCanceled = canceled;
	}

	/**
	 * {@return the canceled state of this event}
	 */
	@ApiStatus.NonExtendable
	default boolean isCanceled() {
		return ((BaseEvent) this).isCanceled;
	}

	/**
	 * Sends the event and returns wether the event was canceled or not.
	 * @return Returns if the event was canceled.
	 */
	default boolean post() {
		((BaseEvent) this).sendEvent();
		return isCanceled();
	}
}
