package io.github.fabricators_of_create.porting_lib.event.common;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.advancement.Advancement;
import net.minecraft.entity.player.PlayerEntity;

public interface AdvancementCallback {

	Event<AdvancementCallback> EVENT = EventFactory.createArrayBacked(AdvancementCallback.class, callbacks -> (player, advancement) -> {
		for (AdvancementCallback e : callbacks)
			e.onAdvancement(player, advancement);
	});

	void onAdvancement(PlayerEntity player, Advancement advancement);
}
