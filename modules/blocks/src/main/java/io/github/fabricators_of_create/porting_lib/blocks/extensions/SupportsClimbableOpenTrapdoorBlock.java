package io.github.fabricators_of_create.porting_lib.blocks.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;

public interface SupportsClimbableOpenTrapdoorBlock {
	/**
	 * Checks if this block makes an open trapdoor above it climbable.
	 *
	 * @param state         The current state
	 * @param level         The current level
	 * @param pos           Block position in level
	 * @param trapdoorState The current state of the open trapdoor above
	 * @return True if the block should act like a ladder
	 */
	default boolean makesOpenTrapdoorAboveClimbable(BlockState state, LevelReader level, BlockPos pos, BlockState trapdoorState) {
		return state.getBlock() instanceof LadderBlock && state.getValue(LadderBlock.FACING) == trapdoorState.getValue(TrapDoorBlock.FACING);
	}
}
