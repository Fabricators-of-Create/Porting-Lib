package io.github.fabricators_of_create.porting_lib.extensions;

import net.minecraft.server.network.ServerPlayerEntity;

public interface PlayerExtensions {
	/**
	 * @return true if this player is both server-sided and fake. Always false for client players.
	 */
	default boolean isFake() {
		return this instanceof ServerPlayerEntity && getClass() != ServerPlayerEntity.class;
	}
}
