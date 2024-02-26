package io.github.fabricators_of_create.porting_lib.entity.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import org.jetbrains.annotations.Nullable;

public interface EntityInteractCallback {
	/**
	 * Fired when an entity is interacted with by a player, from {@link Player#interactOn(Entity, InteractionHand)}
	 */
	Event<EntityInteractCallback> EVENT = EventFactory.createArrayBacked(EntityInteractCallback.class, callbacks -> ((player, hand, target) -> {
		for(EntityInteractCallback e : callbacks) {
			InteractionResult result = e.onEntityInteract(player, hand, target);
			if(result != null)
				return result;
		}
		return null;
	}));

	/**
	 * @return any non-null value to cancel the interaction and replace the result of it
	 */
	@Nullable
	InteractionResult onEntityInteract(Player player, InteractionHand hand, Entity target);
}
