package io.github.fabricators_of_create.porting_lib.extensions.extensions;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.ApiStatus;

public interface MobExtensions {
	/**
	 * This method exists so that spawns can be cancelled from the {@link io.github.fabricators_of_create.porting_lib.event.common.MobSpawnEvents.FinalizeSpawn FinalizeSpawnEvent}
	 * without needing to hook up an additional handler for the {@link io.github.fabricators_of_create.porting_lib.event.common.EntityEvents#ON_JOIN_WORLD}.
	 * @return if this mob will be blocked from spawning during {@link Level#addFreshEntity(Entity)}
	 * @apiNote Not public-facing API.
	 */
	@ApiStatus.Internal
	default boolean isSpawnCancelled() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	/**
	 * Marks this mob as being disallowed to spawn during {@link Level#addFreshEntity(Entity)}.<p>
	 * @throws UnsupportedOperationException if this entity has already been {@link Entity#isAddedToWorld() added to the world}.
	 * @apiNote Not public-facing API.
	 */
	@ApiStatus.Internal
	default void setSpawnCancelled(boolean cancel) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
