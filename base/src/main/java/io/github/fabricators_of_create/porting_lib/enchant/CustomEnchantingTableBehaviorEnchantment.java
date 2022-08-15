package io.github.fabricators_of_create.porting_lib.enchant;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * An enchantment with custom logic for deciding if it can be applied at an
 * enchanting table. Your enchantment must have a corresponding canEnchant as well.
 */
public interface CustomEnchantingTableBehaviorEnchantment {
	default boolean canApplyAtEnchantingTable(ItemStack stack) {
		if (this instanceof Enchantment enchantment) {
			if (stack.getItem() instanceof CustomEnchantingBehaviorItem custom) {
				return custom.canApplyAtEnchantingTable(stack, enchantment);
			}
			return enchantment.category.canEnchant(stack.getItem()); // vanilla Enchantment.canEnchant impl
		}
		throw new RuntimeException("Cannot implement CustomEnchantingTableBehaviorEnchantment on something that isn't an Enchantment");
	}
}
