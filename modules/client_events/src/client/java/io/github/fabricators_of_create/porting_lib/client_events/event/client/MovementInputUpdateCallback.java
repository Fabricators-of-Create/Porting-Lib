package io.github.fabricators_of_create.porting_lib.client_events.event.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.player.Input;
import net.minecraft.world.entity.player.Player;

public interface MovementInputUpdateCallback {
	Event<MovementInputUpdateCallback> EVENT = EventFactory.createArrayBacked(MovementInputUpdateCallback.class, movementInputUpdateCallbacks -> (player, input) -> {
		for (MovementInputUpdateCallback e : movementInputUpdateCallbacks)
			e.onMovementUpdate(player, input);
	});

	void onMovementUpdate(Player player, Input input);
}
