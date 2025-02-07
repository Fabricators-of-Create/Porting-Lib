package io.github.fabricators_of_create.porting_lib.event.common;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.util.ActionResult;

public interface MountEntityCallback {
	Event<MountEntityCallback> EVENT = EventFactory.createArrayBacked(MountEntityCallback.class, callbacks -> (mounted, mounting, isMounting) -> {
		ActionResult result = ActionResult.PASS;
		for (MountEntityCallback callback : callbacks) {
			result = callback.onStartRiding(mounted, mounting, isMounting);
			if (result != ActionResult.PASS) {
				return result;
			}
		}
		return result;
	});

	ActionResult onStartRiding(Entity mounted, Entity mounting, boolean isMounting);
}
