package io.github.fabricators_of_create.porting_lib.core.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import io.github.fabricators_of_create.porting_lib.core.event.object.CancellableEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * Allows event listeners to listen for events even if they were cancelled.
 * For an event to support this, it must check {@link #ignoresCancellation(Event, Object)} to determine
 * if a listener should be invoked or not. This is usually handled by {@link CancellableEvent#shouldInvokeListener(EventHolder, Object)}.
 */
public class CancelBypass {
	private static final Map<Event<?>, List<Object>> listeners = new ConcurrentHashMap<>();

	/**
	 * Mark the listener as ignoring cancellation of the event, and register the listener.
	 */
	public static <T> T register(Event<T> event, T listener) {
		markIgnoring(event, listener);
		event.register(listener);
		return listener;
	}

	/**
	 * Mark the listener as ignoring cancellation of the event.
	 */
	public static <T> void markIgnoring(Event<T> event, T listener) {
		listeners.computeIfAbsent(event, $ -> new ArrayList<>()).add(listener);
	}

	public static <T> boolean ignoresCancellation(Event<T> event, T listener) {
		return listeners.getOrDefault(event, List.of()).contains(listener);
	}

	/**
	 * Create an event that checks for cancel bypassing. The invoker factory gets an {@link EventHolder} for getting the event for querying.
	 * <pre>{@code
	 * public static final Event<MyListener> EVENT = CancelBypass.makeEvent(MyListener.class, event -> callbacks -> (cancellableEvent) -> {
	 *     for (MyListener callback : callbacks) {
	 *         if (cancellableEvent.shouldInvokeListener(event, callback)) {
	 *             callback.onEvent(cancellableEvent);
	 *         }
	 *     }
	 * });
	 * }</pre>
	 */
	public static <T> Event<T> makeEvent(Class<T> type, Function<EventHolder<T>, Function<T[], T>> invokerFactory) {
		EventHolder<T> holder = new EventHolder<>();
		Event<T> event = EventFactory.createArrayBacked(type, invokerFactory.apply(holder));
		holder.event = event;
		return event;
	}

	public static final class EventHolder<T> {
		private Event<T> event;

		public Event<T> get() {
			return event;
		}
	}
}
