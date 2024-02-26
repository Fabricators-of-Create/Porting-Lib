package io.github.fabricators_of_create.porting_lib.mixin.client;

import io.github.fabricators_of_create.porting_lib.extensions.extensions.ParticleExtensions;
import net.minecraft.client.particle.Particle;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(Particle.class)
public abstract class ParticleMixin implements ParticleExtensions {
}
