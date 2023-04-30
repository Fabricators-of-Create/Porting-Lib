package io.github.fabricators_of_create.porting_lib.entity.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;

public class LightningStrikeEvents {
	/**
	 * Fired before a lightning bolt strikes an entity.
	 */
	public static final Event<Before> BEFORE = EventFactory.createArrayBacked(Before.class, callbacks -> (target, lightning) -> {
		for (Before callback : callbacks) {
			if (!callback.canLightningStrike(target, lightning))
				return false;
		}
		return true;
	});

	@FunctionalInterface
	public interface Before {
		/**
		 * @return false to cancel the strike
		 */
		boolean canLightningStrike(Entity target, LightningBolt lightning);
	}

	/**
	 * Fired after a lighting bolt has struck an entity.
	 */
	public static final Event<After> AFTER = EventFactory.createArrayBacked(After.class, callbacks -> (target, lightning) -> {
		for (After callback : callbacks)
			callback.afterLightningStrike(target, lightning);
	});

	@FunctionalInterface
	public interface After {
		void afterLightningStrike(Entity target, LightningBolt lightning);
	}
}
