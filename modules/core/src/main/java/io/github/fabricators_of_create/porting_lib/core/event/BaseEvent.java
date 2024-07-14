package io.github.fabricators_of_create.porting_lib.core.event;

/**
 * Base Event class that all other events are derived from.
 * <br>
 * <strong>Note on abstract events</strong>
 * <p>
 * This is useful for classes that extend {@link BaseEvent} with more data and methods,
 * but should never be listened to directly.
 * <p>
 * For example, an event with {@code Pre} and {@code Post} subclasses might want to
 * be declared as {@code abstract} to prevent user accidentally listening to both.
 * <p>
 * All the parents of abstract event classes until {@link BaseEvent} must also be abstract.
 */
public abstract class BaseEvent {
	boolean isCanceled;

	public abstract void sendEvent();
}
