package io.github.fabricators_of_create.porting_lib.core.event;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import com.google.common.collect.Table;

import io.github.fabricators_of_create.porting_lib.core.event.object.CancellableEvent;
import net.fabricmc.fabric.api.event.Event;

/**
 * Allows event listeners to listen for events even if they were cancelled.
 * For an event to support this, it must check {@link #ignoresCancellation(Class, String, Object)} to determine
 * if a listener should be invoked or not. This is usually handled by {@link CancellableEvent#shouldInvokeListener(Class, String, Object)}.
 */
public class CancelBypass {
	private static final Multimap<Event<?>, Object> listeners = HashMultimap.create();
	private static final Table<Class<?>, String, Event<?>> events = HashBasedTable.create();

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
		listeners.put(event, listener);
	}

	/**
	 * Check if a listener should be invoked or not.
	 * Requires reflection to avoid forward references in event declaration.
	 * @param callbackClass the Class that holds the associated Event field
	 * @param fieldName the name of the public static field that holds the associated Event
	 * @return true if the listener should be invoked regardless of event cancellation
	 */
	public static <T> boolean ignoresCancellation(Class<T> callbackClass, String fieldName, T listener) {
		Event<T> event = getEvent(callbackClass, fieldName);
		return listeners.containsEntry(event, listener);
	}

	/**
	 * Given a class and static field name, get an Event.
	 * This terribleness is needed to avoid forward references in event declaration.
	 * @throws IllegalArgumentException if the field does not exist, is not static, or does not hold an Event
	 * @throws RuntimeException if an {@link IllegalAccessException} occurs
	 */
	@SuppressWarnings("unchecked")
	public static <T> Event<T> getEvent(Class<T> callbackClass, String fieldName) {
		if (!events.contains(callbackClass, fieldName)) {
			String qualified = callbackClass.getName() + '.' + fieldName;
			try {
				Field field = callbackClass.getField(fieldName);
				if (!Modifier.isStatic(field.getModifiers()))
					throw new IllegalArgumentException(qualified + " is not static");
				Object obj = field.get(null);
				if (!(obj instanceof Event<?> event))
					throw new IllegalArgumentException(qualified + " is not an Event");
				events.put(callbackClass, fieldName, event);
			} catch (NoSuchFieldException e) {
				throw new IllegalArgumentException(qualified + " does not exist");
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		return (Event<T>) events.get(callbackClass, fieldName);
	}
}
