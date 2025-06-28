package io.github.fabricators_of_create.porting_lib.entity.injects;

public interface SlimeInjection {
	/**
	 * Called when the slime spawns particles on landing, see onUpdate.
	 * Return true to prevent the spawning of the default particles.
	 */
	default boolean spawnCustomParticles() { return false; }
}
