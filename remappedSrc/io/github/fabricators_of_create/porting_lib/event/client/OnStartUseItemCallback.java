package io.github.fabricators_of_create.porting_lib.event.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

@Environment(EnvType.CLIENT)
public interface OnStartUseItemCallback {
	/**
	 * SUCCESS - swing hand
	 * FAIL - cancel, do nothing
	 * PASS - do nothing
	 */
	Event<OnStartUseItemCallback> EVENT = EventFactory.createArrayBacked(OnStartUseItemCallback.class, callbacks -> (hand) -> {
		for (OnStartUseItemCallback callback : callbacks) {
			ActionResult result = callback.onStartUse(hand);
			if (result != ActionResult.PASS) return result;
		}
		return ActionResult.PASS;
	});

	ActionResult onStartUse(Hand hand);
}
