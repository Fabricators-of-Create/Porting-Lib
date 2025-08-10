package io.github.fabricators_of_create.porting_lib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public interface NeighborChangeListeningBlock {

	/**
	 * Used by the neighbouring blocks to notify this block that that neighbour updated
	 * @param state Block state at position pos
	 * @param world The world
	 * @param pos Block position
	 * @param neighbor Neighbour position
	 */
	void onNeighborChange(BlockState state, LevelReader world, BlockPos pos, BlockPos neighbor);

	/**
	 * Check if this block should be notified of weak changes. <br>
	 * Weak changes are changes 1 block away through a solid block. Similar to comparators.
	 * @param level The world
	 * @param pos Block position
	 * @return true if there are weak changes to check. False otherwise
	 */
	default boolean getWeakChanges(BlockState state, LevelReader level, BlockPos pos) {
		return false;
	}
}
