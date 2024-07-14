package io.github.fabricators_of_create.porting_lib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface CustomRunningEffectsBlock {
	/**
	 * Allows a block to override the standard vanilla running particles.
	 * This is called from Entity.spawnSprintParticle and is called both,
	 * Client and server side, it's up to the implementor to client check / server check.
	 * By default vanilla spawns particles only on the client and the server methods no-op.
	 *
	 * @param state  The BlockState the entity is running on.
	 * @param level  The level.
	 * @param pos    The position at the entities feet.
	 * @param entity The entity running on the block.
	 * @return True to prevent vanilla running particles from spawning.
	 */
	default boolean addRunningEffects(BlockState state, Level level, BlockPos pos, Entity entity) {
		return false;
	}
}
