package io.github.fabricators_of_create.porting_lib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public interface CustomExpBlock {
	/**
	 * Gathers how much experience this block drops when broken.
	 *
	 * @param state The current state
	 * @param level The level
	 * @param randomSource Random source to use for experience randomness
	 * @param pos Block position
	 * @param fortuneLevel fortune enchantment level of tool being used
	 * @param silkTouchLevel silk touch enchantment level of tool being used
	 * @return Amount of XP from breaking this block.
	 */
	default int getExpDrop(BlockState state, LevelReader level, RandomSource randomSource, BlockPos pos, int fortuneLevel, int silkTouchLevel) {
		return 0;
	}
}
