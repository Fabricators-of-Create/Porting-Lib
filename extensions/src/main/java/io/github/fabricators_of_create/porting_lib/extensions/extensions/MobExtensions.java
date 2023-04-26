package io.github.fabricators_of_create.porting_lib.extensions.extensions;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import org.jetbrains.annotations.ApiStatus;

public interface MobExtensions {
	/**
	 * This method exists so that spawns can be cancelled from the {@link net.minecraftforge.event.entity.living.MobSpawnEvent.FinalizeSpawn FinalizeSpawnEvent}
	 * without needing to hook up an additional handler for the {@link net.minecraftforge.event.entity.EntityJoinLevelEvent EntityJoinLevelEvent}.
	 * @return if this mob will be blocked from spawning during {@link Level#addFreshEntity(Entity)}
	 * @apiNote Not public-facing API.
	 */
	@ApiStatus.Internal
	boolean isSpawnCancelled();

	/**
	 * Marks this mob as being disallowed to spawn during {@link Level#addFreshEntity(Entity)}.<p>
	 * @throws UnsupportedOperationException if this entity has already been {@link Entity#isAddedToWorld() added to the world}.
	 * @apiNote Not public-facing API.
	 */
	@ApiStatus.Internal
	void setSpawnCancelled(boolean cancel);
}
