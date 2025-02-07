package io.github.fabricators_of_create.porting_lib.block;

import net.minecraft.block.BlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

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
	default boolean addHitEffects(BlockState state, World level, HitResult target, ParticleManager manager) {
		return false;
	}
}
