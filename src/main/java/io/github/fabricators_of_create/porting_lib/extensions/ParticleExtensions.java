package io.github.fabricators_of_create.porting_lib.extensions;

// TODO: Implement
public interface ParticleExtensions {
	/**
	 * Forge added method that controls if a particle should be culled to it's bounding box.
	 * Default behaviour is culling enabled
	 */
	default boolean shouldCull() {
		return true;
	}
}
