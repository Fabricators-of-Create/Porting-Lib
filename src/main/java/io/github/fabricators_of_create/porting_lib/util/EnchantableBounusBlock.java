package io.github.fabricators_of_create.porting_lib.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

// TODO: implement
public interface EnchantableBounusBlock {
	/**
	 * Determines the amount of enchanting power this block can provide to an enchanting table.
	 * @param level The level
	 * @param pos Block position in level
	 * @return The amount of enchanting power this block produces.
	 */
	default float getEnchantPowerBonus(BlockState state, LevelReader level, BlockPos pos) {
		return state.is(Blocks.BOOKSHELF) ? 1: 0;
	}
}
