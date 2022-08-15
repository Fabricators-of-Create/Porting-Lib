package io.github.fabricators_of_create.porting_lib.event;

import net.minecraft.world.InteractionResult;

public abstract class BaseEvent {
	private InteractionResult result = InteractionResult.PASS;
	private boolean canceled;

	/**
	 * Returns the value set as the result of this event
	 */
	public InteractionResult getResult() {
		return result;
	}

	/**
	 * Sets the result value for this event, not all events can have a result set, and any attempt to
	 * set a result for an event that isn't expecting it will result in a IllegalArgumentException.
	 *
	 * The functionality of setting the result is defined on a per-event basis.
	 *
	 * @param value The new result
	 */
	public void setResult(InteractionResult value) {
		result = value;
	}

	public void setCanceled(boolean cancelled) {
		this.canceled = cancelled;
	}

	public boolean isCanceled() {
		return canceled;
	}

	public abstract void sendEvent();
}
