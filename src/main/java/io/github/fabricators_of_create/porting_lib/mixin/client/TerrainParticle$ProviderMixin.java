package io.github.fabricators_of_create.porting_lib.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;

@Mixin(TerrainParticle.Provider.class)
public class TerrainParticle$ProviderMixin {
	@ModifyReturnValue(method = "createParticle", at = @At("RETURN"))
	private Particle updateSprite(Particle particle,
								  BlockParticleOption type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
		BlockPos source = type.getSourcePos();
		if (source != null && particle instanceof TerrainParticle terrainParticle) {
			terrainParticle.updateSprite(type.getState(), source);
		}
		return particle;
	}
}
