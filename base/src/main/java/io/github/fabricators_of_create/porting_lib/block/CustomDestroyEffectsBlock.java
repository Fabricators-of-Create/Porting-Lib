package io.github.fabricators_of_create.porting_lib.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public interface CustomDestroyEffectsBlock {
	/**
	 * Spawn particles for when the block is destroyed. Due to the nature
	 * of how this is invoked, the x/y/z locations are not always guaranteed
	 * to host your block. So be sure to do proper sanity checks before assuming
	 * that the location is this block.
	 *
	 * @param Level   The current Level
	 * @param pos     Position to spawn the particle
	 * @param engine  A reference to the current particle engine.
	 * @return True to prevent vanilla break particles from spawning.
	 */
	@Environment(EnvType.CLIENT)
	default boolean addDestroyEffects(BlockState state, ClientLevel Level, BlockPos pos, ParticleEngine engine) {
		return !state.shouldSpawnTerrainParticles();
	}
}
