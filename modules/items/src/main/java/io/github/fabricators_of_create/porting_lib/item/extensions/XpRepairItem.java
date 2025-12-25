package io.github.fabricators_of_create.porting_lib.item.extensions;

import net.minecraft.world.item.ItemStack;

public interface XpRepairItem {
	/**
	 * Determines the amount of durability the mending enchantment
	 * will repair, on average, per 0.5 points of experience.
	 */
	default float getXpRepairRatio(ItemStack stack) {
		return 1f;
	}
}
