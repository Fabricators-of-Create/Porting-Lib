package io.github.fabricators_of_create.porting_lib.block;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;

public interface CustomHitEffectsBlock {
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
}
