package io.github.fabricators_of_create.porting_lib.item.extensions;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface CustomEnchantmentValueItem {
	/**
	 * ItemStack sensitive version of {@link Item#getEnchantmentValue()}.
	 *
	 * @param stack The ItemStack
	 * @return the enchantment value
	 */
	int getEnchantmentValue(ItemStack stack);
}
