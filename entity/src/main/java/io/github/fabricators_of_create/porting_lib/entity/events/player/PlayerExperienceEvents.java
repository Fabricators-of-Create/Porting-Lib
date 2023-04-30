package io.github.fabricators_of_create.porting_lib.entity.events.player;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;

public class PlayerExperienceEvents {
	/**
	 * Fired when a player tries to pick up an Experience Orb.
	 */
	public static final Event<PlayerExpPickup> EXP_PICKUP = EventFactory.createArrayBacked(PlayerExpPickup.class, callbacks -> (player, orb) -> {
		for (PlayerExpPickup e : callbacks)
			if (!e.onExpPickup(player, orb))
				return false;
		return true;
	});

	@FunctionalInterface
	public interface PlayerExpPickup {
		/**
		 * @return false to prevent picking up the experience
		 */
		boolean onExpPickup(Player player, ExperienceOrb orb);
	}

	/**
	 * Fired when a player is granted experience points.
	 * This event is chained; multiple listeners may modify the amount.
	 */
	public static final Event<PlayerXpChange> EXP_GRANT = EventFactory.createArrayBacked(PlayerXpChange.class, callbacks -> (player, addedExp) -> {
		for (PlayerXpChange callback : callbacks)
			addedExp = callback.modifyExpChange(player, addedExp);
		return addedExp;
	});

	@FunctionalInterface
	public interface PlayerXpChange {
		/**
		 * @return the amount of experience to grant the player, or the original if unmodified
		 */
		int modifyExpChange(Player player, int addedExp);
	}
}
