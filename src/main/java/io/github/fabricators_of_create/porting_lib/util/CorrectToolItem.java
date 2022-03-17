package io.github.fabricators_of_create.porting_lib.util;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public interface CorrectToolItem {
	/**
	 * ItemStack sensitive version of {@link Item#isCorrectToolForDrops(BlockState)}
	 *
	 * @param stack The itemstack used to harvest the block
	 * @param state The block trying to harvest
	 * @return true if the stack can harvest the block
	 */
	default boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
		return ((Item) this).isCorrectToolForDrops(state);
	}
}
