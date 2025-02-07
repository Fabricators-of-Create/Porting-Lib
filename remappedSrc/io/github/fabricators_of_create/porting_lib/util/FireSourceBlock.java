package io.github.fabricators_of_create.porting_lib.util;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldView;

// TODO: implement (ASM)
public interface FireSourceBlock {
	/**
	 * Currently only called by fire when it is on top of this block.
	 * Returning true will prevent the fire from naturally dying during updating.
	 * Also prevents firing from dying from rain.
	 *
	 * @param state The current state
	 * @param level The current level
	 * @param pos Block position in level
	 * @param direction The direction that the fire is coming from
	 * @return True if this block sustains fire, meaning it will never go out.
	 */
	default boolean isFireSource(BlockState state, WorldView level, BlockPos pos, Direction direction) {
		return state.isIn(level.getDimension().getInfiniburnBlocks());
	}
}
