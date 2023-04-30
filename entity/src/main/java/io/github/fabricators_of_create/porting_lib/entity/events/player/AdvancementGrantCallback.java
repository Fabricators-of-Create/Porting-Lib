package io.github.fabricators_of_create.porting_lib.entity.events.player;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.advancements.Advancement;
import net.minecraft.world.entity.player.Player;

public interface AdvancementGrantCallback {
	/**
	 * Fired when a player is granted an advancement.
	 */
	Event<AdvancementGrantCallback> EVENT = EventFactory.createArrayBacked(AdvancementGrantCallback.class, callbacks -> (player, advancement) -> {
		for (AdvancementGrantCallback e : callbacks)
			e.onGrantAdvancement(player, advancement);
	});

	void onGrantAdvancement(Player player, Advancement advancement);
}
