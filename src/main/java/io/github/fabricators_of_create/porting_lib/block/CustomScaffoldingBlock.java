package io.github.fabricators_of_create.porting_lib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public interface CustomScaffoldingBlock {
	/**
	 * Normally, sneaking while sliding down a climbable block will stop sliding.
	 * On scaffolding, this is not the case, and sneaking is ignored.
	 * This is because on scaffolding, sneaking is how you descend.
	 * @return true if this block should act as scaffolding, ignoring sneaking
	 */
	boolean isScaffolding(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity);
}
