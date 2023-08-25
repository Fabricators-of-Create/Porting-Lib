package io.github.fabricators_of_create.porting_lib.blocks.api.addons;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface CustomRunningEffectsBlock {
	/**
	 * @return true to prevent vanilla particles spawning
	 */
	boolean addRunningEffects(BlockState state, Level world, BlockPos pos, Entity entity);
}
