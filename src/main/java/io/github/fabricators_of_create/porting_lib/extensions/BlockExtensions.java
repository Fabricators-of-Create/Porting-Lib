package io.github.fabricators_of_create.porting_lib.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public interface BlockExtensions {
	/**
	 * Gathers how much experience this block drops when broken.
	 *
	 * @param state The current state
	 * @param world The world
	 * @param pos Block position
	 * @param fortune
	 * @return Amount of XP from breaking this block.
	 */
	default int getExpDrop(BlockState state, LevelReader world, BlockPos pos, int fortune, int silktouch) {
		return 0;
	}
}
