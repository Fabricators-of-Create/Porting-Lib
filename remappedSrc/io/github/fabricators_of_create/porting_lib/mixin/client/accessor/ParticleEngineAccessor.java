package io.github.fabricators_of_create.porting_lib.mixin.client.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleManager;

@Environment(EnvType.CLIENT)
@Mixin(ParticleManager.class)
public interface ParticleEngineAccessor {
	@Accessor("providers")
	Int2ObjectMap<ParticleFactory<?>> port_lib$getProviders();
}
