package io.github.fabricators_of_create.porting_lib.blocks.impl.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import io.github.fabricators_of_create.porting_lib.blocks.api.addons.CustomDestroyEffectsBlock;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

@Environment(EnvType.CLIENT)
@Mixin(ParticleEngine.class)
public class ParticleEngineMixin {
	@Shadow
	protected ClientLevel level;

	@ModifyExpressionValue(method = "destroy", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;shouldSpawnTerrainParticles()Z"))
	private boolean port_lib$customDestroyEffects(boolean original, BlockPos blockPos, BlockState blockState) {
		if (blockState.getBlock() instanceof CustomDestroyEffectsBlock custom) {
			if (!custom.addDestroyEffects(blockState, level, blockPos, (ParticleEngine) (Object) this)) {
				return false;
			}
		}
		return original;
	}
}
