package io.github.fabricators_of_create.porting_lib.event.common;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.advancements.Advancement;
import net.minecraft.world.entity.player.Player;

public interface AdvancementCallback {

	Event<AdvancementCallback> EVENT = EventFactory.createArrayBacked(AdvancementCallback.class, callbacks -> (player, advancement) -> {
		for (AdvancementCallback e : callbacks)
			e.onAdvancement(player, advancement);
	});

	void onAdvancement(Player player, Advancement advancement);
}
