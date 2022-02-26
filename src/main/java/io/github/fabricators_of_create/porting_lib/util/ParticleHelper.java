package io.github.fabricators_of_create.porting_lib.util;

import io.github.fabricators_of_create.porting_lib.mixin.client.accessor.ParticleAccessor;

import net.minecraft.client.particle.Particle;

public final class ParticleHelper {

	public static void setStoppedByCollision(Particle particle, boolean bool) {
		((ParticleAccessor) particle).port_lib$stoppedByCollision(bool);
	}

	private ParticleHelper() {}
}
