package io.github.fabricators_of_create.porting_lib.event;

public class CancellableEvent {
	private boolean canceled;

	public void setCanceled(boolean cancelled) {
		this.canceled = cancelled;
	}

	public boolean isCanceled() {
		return canceled;
	}
}
