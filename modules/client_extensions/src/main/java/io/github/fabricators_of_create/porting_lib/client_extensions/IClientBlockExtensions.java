package io.github.fabricators_of_create.porting_lib.client_extensions;

import net.fabricmc.api.EnvType;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

/**
 * {@linkplain EnvType#CLIENT Client-only} extensions to {@link Block}.
 */
public interface IClientBlockExtensions {
	IClientBlockExtensions DEFAULT = new IClientBlockExtensions() {};

	static IClientBlockExtensions of(BlockState state) {
		return of(state.getBlock());
	}

	static IClientBlockExtensions of(Block block) {
		return ClientExtensionsRegistry.BLOCK_EXTENSIONS.getOrDefault(block, DEFAULT);
	}

	static boolean exists(BlockState state) {
		return exists(state.getBlock());
	}

	static boolean exists(Block block) {
		return ClientExtensionsRegistry.BLOCK_EXTENSIONS.containsKey(block);
	}

	/**
	 * Spawn a digging particle effect in the level, this is a wrapper
	 * around EffectRenderer.addBlockHitEffects to allow the block more
	 * control over the particles. Useful when you have entirely different
	 * texture sheets for different sides/locations in the level.
	 *
	 * @param state   The current state
	 * @param level   The current level
	 * @param target  The target the player is looking at {x/y/z/side/sub}
	 * @param manager A reference to the current particle manager.
	 * @return True to prevent vanilla digging particles form spawning.
	 */
	default boolean addHitEffects(BlockState state, Level level, HitResult target, ParticleEngine manager) {
		return false;
	}

	/**
	 * Spawn particles for when the block is destroyed. Due to the nature
	 * of how this is invoked, the x/y/z locations are not always guaranteed
	 * to host your block. So be sure to do proper sanity checks before assuming
	 * that the location is this block.
	 *
	 * @param Level   The current Level
	 * @param pos     Position to spawn the particle
	 * @param manager A reference to the current particle manager.
	 * @return True to prevent vanilla break particles from spawning.
	 */
	default boolean addDestroyEffects(BlockState state, Level Level, BlockPos pos, ParticleEngine manager) {
		return !state.shouldSpawnTerrainParticles();
	}
}
