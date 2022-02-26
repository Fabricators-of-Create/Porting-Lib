package io.github.fabricators_of_create.porting_lib.event;

import javax.annotation.Nullable;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

@FunctionalInterface
public interface OnDatapackSyncCallback {
	Event<OnDatapackSyncCallback> EVENT = EventFactory.createArrayBacked(OnDatapackSyncCallback.class, callbacks -> ((playerList, player) -> {
		for (OnDatapackSyncCallback event : callbacks)
			event.onDatapackSync(playerList, player);
	}));

	void onDatapackSync(PlayerList playerList, @Nullable ServerPlayer player);
}
