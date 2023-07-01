package io.github.fabricators_of_create.porting_lib.core.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.github.fabricators_of_create.porting_lib.core.event.object.CancellableEvent;
import net.fabricmc.fabric.api.event.Event;

/**
 * Allows event listeners to listen for events even if they were cancelled.
 * For an event to support this, it must check {@link #ignoresCancellation(Event, Object)} to determine
 * if a listener should be invoked or not. This is usually handled by {@link CancellableEvent#shouldInvokeListener(Event, Object)}.
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
}
