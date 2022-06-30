package io.github.fabricators_of_create.porting_lib.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface UsingTickItem {
	/**
	 * Called each tick while using an item.
	 *
	 * @param stack  The Item being used
	 * @param player The Player using the item
	 * @param count  The amount of time in tick the item has been used for
	 *               continuously
	 */
	default void onUsingTick(ItemStack stack, LivingEntity player, int count) {}
}
