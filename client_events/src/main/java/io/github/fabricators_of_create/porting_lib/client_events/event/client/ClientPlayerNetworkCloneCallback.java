package io.github.fabricators_of_create.porting_lib.client_events.event.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.CommonPlayerSpawnInfo;

public interface ClientPlayerNetworkCloneCallback {
	/**
	 * Fired when the client player respawns, creating a new player instance to replace the old player instance.
	 */
	Event<ClientPlayerNetworkCloneCallback> EVENT = EventFactory.createArrayBacked(ClientPlayerNetworkCloneCallback.class, callbacks -> (pc, spawnInfo, oldPlayer, newPlayer, networkManager) -> {
		for (ClientPlayerNetworkCloneCallback e : callbacks)
			e.onPlayerRespawn(pc, spawnInfo, oldPlayer, newPlayer, networkManager);
	});

	/**
	 * @param multiPlayerGameMode The multiplayer game mode controller for the player.
	 * @param spawnInfo The current info used to spawn the new player.
	 * @param oldPlayer The previous player instance.
	 * @param newPlayer The newly created player instance.
	 * @param networkManager The network connection for the player.
	 */
	void onPlayerRespawn(final MultiPlayerGameMode multiPlayerGameMode, CommonPlayerSpawnInfo spawnInfo, final LocalPlayer oldPlayer, final LocalPlayer newPlayer, final Connection networkManager);
}
