package io.github.fabricators_of_create.porting_lib.event.common;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

/**
 * Use {@link ServerLifecycleEvents.SyncDataPackContents}
 * */
@Deprecated(forRemoval = true)
@FunctionalInterface
public interface OnDatapackSyncCallback {
	Event<OnDatapackSyncCallback> EVENT = EventFactory.createArrayBacked(OnDatapackSyncCallback.class, callbacks -> ((playerList, player) -> {
		for (OnDatapackSyncCallback event : callbacks)
			event.onDatapackSync(playerList, player);
	}));

	void onDatapackSync(PlayerManager playerList, @Nullable ServerPlayerEntity player);
}
