package io.github.fabricators_of_create.porting_lib.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;

public class PlayerTickEvents {
	public static final Event<Start> START = EventFactory.createArrayBacked(Start.class, callbacks -> (player) -> {
		for (Start callback : callbacks) {
			callback.onStartOfPlayerTick(player);
		}
	});

	public static final Event<End> END = EventFactory.createArrayBacked(End.class, callbacks -> (player) -> {
		for (End callback : callbacks) {
			callback.onEndOfPlayerTick(player);
		}
	});

	@FunctionalInterface
	interface End {
		void onEndOfPlayerTick(Player player);
	}

	@FunctionalInterface
	interface Start {
		void onStartOfPlayerTick(Player player);
	}
}
