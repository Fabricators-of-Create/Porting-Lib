package io.github.fabricators_of_create.porting_lib.block;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;

public interface CustomDestroyEffectsBlock {
	/**
	 * Custom effects when your block is broken.
	 * @return true to cancel vanilla effects
	 */
	@Environment(EnvType.CLIENT)
	boolean applyCustomDestroyEffects(BlockState state, ClientWorld Level, BlockPos pos, ParticleManager engine);
}
