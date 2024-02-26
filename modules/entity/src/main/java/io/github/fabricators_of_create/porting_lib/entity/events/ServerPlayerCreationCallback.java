package io.github.fabricators_of_create.porting_lib.entity.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;

public interface ServerPlayerCreationCallback {
	Event<ServerPlayerCreationCallback> EVENT = EventFactory.createArrayBacked(ServerPlayerCreationCallback.class, callbacks -> (player) -> {
		for (ServerPlayerCreationCallback callback : callbacks) {
			callback.onCreate(player);
		}
	});

	void onCreate(ServerPlayer player);
}
