package io.github.fabricators_of_create.porting_lib.event.common;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public interface EntityInteractCallback {
	Event<EntityInteractCallback> EVENT = EventFactory.createArrayBacked(EntityInteractCallback.class, callbacks -> ((player, hand, target) -> {
		for(EntityInteractCallback e : callbacks) {
			ActionResult result = e.onEntityInteract(player, hand, target);
			if(result != null)
				return result;
		}
		return null;
	}));

	ActionResult onEntityInteract(PlayerEntity player, Hand hand, Entity target);
}
