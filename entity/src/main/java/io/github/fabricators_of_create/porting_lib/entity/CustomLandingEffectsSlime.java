package io.github.fabricators_of_create.porting_lib.entity;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.monster.Slime;

/**
 * A {@link Slime} that has custom effects when it lands on the ground.
 */
public interface CustomLandingEffectsSlime {
	/**
	 * Called when a slime lands on the ground. Normally, this spawns particles,
	 * plays a sound, and sets the squish animation target.
	 * @return true to cancel all other landing behavior
	 */
	default boolean onLand() {
		return false;
	}

	/**
	 * Allows for spawning custom particles when the slime lands.
	 * @return true to cancel vanilla particles
	 */
	default boolean spawnLandingParticles() {
		return false;
	}

	/**
	 * Allows for playing a custom sound on landing.
	 * @return true to cancel the vanilla squish sound
	 */
	default boolean playLandingSound(SoundEvent squishSound, float volume, float pitch) {
		return false;
	}
}
