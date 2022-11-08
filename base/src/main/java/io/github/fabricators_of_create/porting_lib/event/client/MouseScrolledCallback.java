package io.github.fabricators_of_create.porting_lib.event.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

/**
 * @deprecated switch to {@link MouseInputEvents}
 */
@Deprecated(forRemoval = true)
@Environment(EnvType.CLIENT)
public interface MouseScrolledCallback {
	/**
	 * Fired whenever the mouse scroll wheel is scrolled.
	 */
	Event<MouseScrolledCallback> EVENT = EventFactory.createArrayBacked(MouseScrolledCallback.class, callbacks -> delta -> {
		for (MouseScrolledCallback callback : callbacks) {
			if (callback.onMouseScrolled(delta)) {
				return true;
			}
		}
		return false;
	});

	/**
	 * @param delta amount scrolled
	 * @return true to cancel scroll, otherwise false
	 */
	boolean onMouseScrolled(double delta);
}
