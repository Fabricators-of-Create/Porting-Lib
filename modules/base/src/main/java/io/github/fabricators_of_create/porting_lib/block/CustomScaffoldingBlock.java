package io.github.fabricators_of_create.porting_lib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public interface CustomScaffoldingBlock {
	/**
	 * Normally, sneaking while sliding down a climbable block will stop sliding.
	 * On scaffolding, this is not the case, and sneaking is ignored.
	 * This is because on scaffolding, sneaking is how you descend.
	 * Checks if a player or entity handles movement on this block like scaffolding.
	 *
	 * @param state The current state
	 * @param level The current level
	 * @param pos The block position in level
	 * @param entity The entity on the scaffolding
	 * @return True if the block should act like scaffolding
	 */
	default boolean isScaffolding(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
		return state.is(Blocks.SCAFFOLDING);
	}
}
