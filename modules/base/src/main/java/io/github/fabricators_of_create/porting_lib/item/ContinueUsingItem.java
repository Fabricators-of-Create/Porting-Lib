package io.github.fabricators_of_create.porting_lib.item;

import net.minecraft.world.item.ItemStack;

public interface ContinueUsingItem {
	/**
	 * Called while an item is in 'active' use to determine if usage should
	 * continue. Allows items to continue being used while sustaining damage, for
	 * example.
	 *
	 * @param oldStack the previous 'active' stack
	 * @param newStack the stack currently in the active hand
	 * @return true to set the new stack to active and continue using it
	 */
	default boolean canContinueUsing(ItemStack oldStack, ItemStack newStack) {
		return ItemStack.isSameItem(oldStack, newStack);
	}
}
