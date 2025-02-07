package io.github.fabricators_of_create.porting_lib.enchant;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public interface EnchantmentBonusBlock {
	/**
	 * Determines the amount of enchanting power this block can provide to an enchanting table.
	 * @param level The level
	 * @param pos Block position in level
	 * @return The amount of enchanting power this block produces.
	 */
	default float getEnchantPowerBonus(BlockState state, WorldView level, BlockPos pos) {
		return state.isOf(Blocks.BOOKSHELF) ? 1 : 0;
	}
}
