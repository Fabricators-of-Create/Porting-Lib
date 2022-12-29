package io.github.fabricators_of_create.porting_lib.fake_players.extensions;

import net.minecraft.server.level.ServerPlayer;

public interface PlayerExtensions {
	/**
	 * @return true if this player is both server-sided and fake. Always false for client players.
	 */
	default boolean isFake() {
		return this instanceof ServerPlayer && getClass() != ServerPlayer.class;
	}
}
