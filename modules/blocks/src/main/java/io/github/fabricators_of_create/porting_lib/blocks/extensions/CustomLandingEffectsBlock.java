package io.github.fabricators_of_create.porting_lib.blocks.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;

public interface CustomLandingEffectsBlock {
	/**
	 * Allows a block to override the standard EntityLivingBase.updateFallState
	 * particles, this is a server side method that spawns particles with
	 * WorldServer.spawnParticle.
	 *
	 * @param level             The current server level
	 * @param pos               The position of the block.
	 * @param state2            The state at the specific level/pos
	 * @param entity            The entity that hit landed on the block
	 * @param numberOfParticles That vanilla level have spawned
	 * @return True to prevent vanilla landing particles from spawning
	 */
	default boolean addLandingEffects(BlockState state1, ServerLevel level, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles) {
		return false;
	}
}
