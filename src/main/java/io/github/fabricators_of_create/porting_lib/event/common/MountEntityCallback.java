package io.github.fabricators_of_create.porting_lib.event.common;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;

public interface MountEntityCallback {
	Event<MountEntityCallback> EVENT = EventFactory.createArrayBacked(MountEntityCallback.class, callbacks -> (mounted, mounting, isMounting) -> {
		InteractionResult result = InteractionResult.PASS;
		for (MountEntityCallback callback : callbacks) {
			result = callback.onStartRiding(mounted, mounting, isMounting);
			if (result != InteractionResult.PASS) {
				return result;
			}
		}
		return result;
	});

	InteractionResult onStartRiding(Entity mounted, Entity mounting, boolean isMounting);
}
