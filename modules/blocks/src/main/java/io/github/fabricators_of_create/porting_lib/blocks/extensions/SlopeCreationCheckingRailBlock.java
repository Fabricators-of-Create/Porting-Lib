package io.github.fabricators_of_create.porting_lib.blocks.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface SlopeCreationCheckingRailBlock {
	/**
	 * Returns true if the rail can make up and down slopes.
	 * Used by placement logic.
	 *
	 * @param level The level.
	 * @param pos   Block's position in level
	 * @return True if the rail can make slopes.
	 */
	default boolean canMakeSlopes(BlockState state, BlockGetter level, BlockPos pos) {
		return true;
	}
}
