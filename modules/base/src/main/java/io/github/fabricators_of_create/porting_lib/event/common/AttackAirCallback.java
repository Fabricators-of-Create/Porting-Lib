package io.github.fabricators_of_create.porting_lib.event.common;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.player.LocalPlayer;

// TODO 1.20: rename to AttackMissedCallback or similar
/**
 * An Event fired when an attack hits nothing.
 */
@Environment(EnvType.CLIENT)
public interface AttackAirCallback {
	Event<AttackAirCallback> EVENT = EventFactory.createArrayBacked(AttackAirCallback.class, callbacks -> (player) -> {
		for (AttackAirCallback callback : callbacks) {
			callback.attackAir(player);
		}
	});

	void attackAir(LocalPlayer player);
}
