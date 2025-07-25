package io.github.fabricators_of_create.porting_lib.enchant;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

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

	/**
	 * Gets the level of the enchantment currently present on the stack. By default, returns the enchantment level present in NBT.
	 * Most enchantment implementations rely upon this method.
	 * The returned value must be the same as getting the enchantment from {@link #getAllEnchantments}
	 *
	 * @param stack       The item stack being checked
	 * @param enchantment The enchantment being checked for
	 * @return Level of the enchantment, or 0 if not present
	 * @see #getAllEnchantments
	 */
	default int getEnchantmentLevel(ItemStack stack, Holder<Enchantment> enchantment) {
		ItemEnchantments itemenchantments = stack.getEnchantments();
		return itemenchantments.getLevel(enchantment);
	}

	/**
	 * Gets a map of all enchantments present on the stack. By default, returns the enchantments present in NBT.
	 * Used in several places in code including armor enchantment hooks.
	 * The returned value(s) must have the same level as {@link #getEnchantmentLevel}.
	 *
	 * @param stack  The item stack being checked
	 * @param lookup A registry lookup, used to resolve enchantment {@link Holder}s.
	 * @return Map of all enchantments on the stack, empty if no enchantments are present
	 * @see #getEnchantmentLevel
	 */
	default ItemEnchantments getAllEnchantments(ItemStack stack, HolderLookup.RegistryLookup<Enchantment> lookup) {
		return stack.getEnchantments();
	}
}
