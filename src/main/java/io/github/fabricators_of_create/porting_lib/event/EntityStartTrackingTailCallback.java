package io.github.fabricators_of_create.porting_lib.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public interface EntityStartTrackingTailCallback {
	Event<EntityStartTrackingTailCallback> EVENT = EventFactory.createArrayBacked(EntityStartTrackingTailCallback.class, callbacks -> (entity, player) -> {
		for (EntityStartTrackingTailCallback callback : callbacks) {
			callback.onTrackingStart(entity, player);
		}
	});

	void onTrackingStart(Entity tracking, ServerPlayer player);
}
