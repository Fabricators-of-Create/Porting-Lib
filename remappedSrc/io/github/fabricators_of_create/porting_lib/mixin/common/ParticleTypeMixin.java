package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import io.github.fabricators_of_create.porting_lib.extensions.RegistryNameProvider;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@Mixin(ParticleType.class)
public abstract class ParticleTypeMixin implements RegistryNameProvider {
	@Unique
	private Identifier port_lib$registryName = null;

	@Override
	public Identifier getRegistryName() {
		if (port_lib$registryName == null) {
			port_lib$registryName = Registry.PARTICLE_TYPE.getId((ParticleType<?>) (Object) this);
		}
		return port_lib$registryName;
	}
}
