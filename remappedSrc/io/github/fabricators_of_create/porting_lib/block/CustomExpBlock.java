package io.github.fabricators_of_create.porting_lib.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public interface CustomExpBlock {
	/**
	 * Gathers how much experience this block drops when broken.
	 *
	 * @param state The current state
	 * @param world The world
	 * @param pos Block position
	 * @param fortune
	 * @return Amount of XP from breaking this block.
	 */
	default int getExpDrop(BlockState state, WorldView world, BlockPos pos, int fortune, int silktouch) {
		return 0;
	}
}
