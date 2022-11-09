package io.github.fabricators_of_create.porting_lib.event.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionResult;

/**
 * @deprecated switch to {@link MouseInputEvents}, this doesn't even work right
 */
@Deprecated(forRemoval = true)
@Environment(EnvType.CLIENT)
public interface MouseButtonCallback {
	/**
	 * action:
	 * 1 -> press
	 * 0 -> release
	 * 2 -> repeat
	 */
	Event<MouseButtonCallback> EVENT = EventFactory.createArrayBacked(MouseButtonCallback.class, callbacks -> (button, action, mods) -> {
		for (MouseButtonCallback callback : callbacks) {
			InteractionResult result = callback.onMouseButton(button, action, mods);
			if (result != InteractionResult.PASS) return result;
		}
		return InteractionResult.PASS;
	});

	InteractionResult onMouseButton(int button, int action, int mods);
}
