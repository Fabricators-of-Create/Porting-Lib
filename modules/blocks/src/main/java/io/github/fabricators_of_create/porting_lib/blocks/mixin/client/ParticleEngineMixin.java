package io.github.fabricators_of_create.porting_lib.blocks.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import io.github.fabricators_of_create.porting_lib.blocks.extensions.CustomDestroyEffectsBlock;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ParticleEngine.class)
public class ParticleEngineMixin {
	@Shadow
	protected ClientLevel level;

	@ModifyExpressionValue(method = "destroy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;shouldSpawnTerrainParticles()Z"))
	private boolean customDestroyEffects(boolean original, BlockPos blockPos, BlockState blockState) {
		if (blockState.getBlock() instanceof CustomDestroyEffectsBlock custom) {
			if (!custom.addDestroyEffects(blockState, level, blockPos, (ParticleEngine) (Object) this)) {
				return false;
			}
		}
		return original;
	}
}
