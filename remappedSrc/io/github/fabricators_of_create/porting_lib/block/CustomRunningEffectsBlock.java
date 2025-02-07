package io.github.fabricators_of_create.porting_lib.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface CustomRunningEffectsBlock {
	/**
	 * @return true to prevent vanilla particles spawning
	 */
	boolean addRunningEffects(BlockState state, World world, BlockPos pos, Entity entity);
}
