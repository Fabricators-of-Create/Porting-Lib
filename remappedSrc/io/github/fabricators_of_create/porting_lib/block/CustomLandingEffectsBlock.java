package io.github.fabricators_of_create.porting_lib.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public interface CustomLandingEffectsBlock {
	/**
	 * @return true to prevent vanilla particles spawning
	 */
	boolean addLandingEffects(BlockState state1, ServerWorld level, BlockPos pos, BlockState state2, LivingEntity entity, int numberOfParticles);
}
