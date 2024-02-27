package io.github.fabricators_of_create.porting_lib.block;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public interface StickToBlock {
	/**
	 * Determines if this block can stick to another block when pushed by a piston.
	 *
	 * @param state My state
	 * @param other Other block
	 * @return True to link blocks
	 */
	default boolean canStickTo(BlockState state, BlockState other) {
		if (state.getBlock() == Blocks.HONEY_BLOCK && other.getBlock() == Blocks.SLIME_BLOCK) return false;
		if (state.getBlock() == Blocks.SLIME_BLOCK && other.getBlock() == Blocks.HONEY_BLOCK) return false;
		return state.isStickyBlock() || other.isStickyBlock();
	}
}
