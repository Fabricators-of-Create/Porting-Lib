package io.github.fabricators_of_create.porting_lib.enchant;

import net.minecraft.world.item.ItemStack;

public interface CustomEnchantingBehaviorItem {
	/**
	 * Allow or forbid the specific book/item combination as an anvil enchant
	 *
	 * @param stack The item
	 * @param book  The book
	 * @return if the enchantment is allowed
	 */
	default boolean isBookEnchantable(ItemStack stack, ItemStack book) {
		return true;
	}
}
