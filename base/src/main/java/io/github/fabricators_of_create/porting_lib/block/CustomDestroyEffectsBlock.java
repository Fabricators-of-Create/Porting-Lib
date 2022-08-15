package io.github.fabricators_of_create.porting_lib.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public interface CustomDestroyEffectsBlock {
	/**
	 * Custom effects when your block is broken.
	 * @return true to cancel vanilla effects
	 */
	@Environment(EnvType.CLIENT)
	boolean applyCustomDestroyEffects(BlockState state, ClientLevel Level, BlockPos pos, ParticleEngine engine);
}
