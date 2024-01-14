package io.github.fabricators_of_create.porting_lib.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

public interface CustomEnchantmentLevelItem {
	/**
	 * Modify the found level of an enchantment on this item.
	 * @return the new level, or 'level' if unchanged
	 */
	int modifyEnchantmentLevel(ItemStack stack, Enchantment enchantment, int level);
}
