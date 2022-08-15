package io.github.fabricators_of_create.porting_lib.block;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public interface StickyBlock {
	/**
	 * @param state The state
	 * @return true if the block is sticky block which used for pull or push adjacent blocks (use by piston)
	 */
	default boolean isStickyBlock(BlockState state) {
		return state.getBlock() == Blocks.SLIME_BLOCK || state.getBlock() == Blocks.HONEY_BLOCK;
	}
}
