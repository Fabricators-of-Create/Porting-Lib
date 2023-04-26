package io.github.fabricators_of_create.porting_lib.event.common;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public interface EntityInteractCallback {
	/**
	 * Use {@link PlayerInteractionEvents#ENTITY_INTERACT} instead.
	 */
	@Deprecated(forRemoval = true)
	Event<EntityInteractCallback> EVENT = EventFactory.createArrayBacked(EntityInteractCallback.class, callbacks -> ((player, hand, target) -> {
		for(EntityInteractCallback e : callbacks) {
			InteractionResult result = e.onEntityInteract(player, hand, target);
			if(result != null)
				return result;
		}
		return null;
	}));

	InteractionResult onEntityInteract(Player player, InteractionHand hand, Entity target);
}
