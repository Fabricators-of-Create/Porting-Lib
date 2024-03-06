package io.github.fabricators_of_create.porting_lib.models.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(ParticleEngine.class)
public class ParticleEngineMixin {
	@ModifyArgs(
			method = "crack",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/particle/ParticleEngine;add(Lnet/minecraft/client/particle/Particle;)V"
			)
	)
	private void updateSpriteOnCrack(Args args, BlockPos pos, Direction side, @Local(ordinal = 0) BlockState state) {
		Particle particle = args.get(0);
		if (particle instanceof TerrainParticle terrainParticle)
			terrainParticle.updateSprite(state, pos);
	}

	@ModifyArgs(
			method = "method_34020",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/particle/ParticleEngine;add(Lnet/minecraft/client/particle/Particle;)V"
			)
	)
	private void updateSpriteOnDestroy(Args args, BlockPos pos, BlockState state,
									   double dx, double e, double f, double g, double h, double i) {
		Particle particle = args.get(0);
		if (particle instanceof TerrainParticle terrainParticle)
			terrainParticle.updateSprite(state, pos);
	}
}
