package io.github.fabricators_of_create.porting_lib.item.extensions;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Optional;

public interface CustomSupportsEnchantItem {
	/**
	 * Checks if an item should be treated as a primary item for a given enchantment.
	 * <p>
	 * Primary items are those that are able to receive the enchantment during enchanting,
	 * either from the enchantment table or other random enchantment mechanisms.
	 * As a special case, books are primary items for every enchantment.
	 * <p>
	 * Other application mechanisms, such as the anvil, check {@link #supportsEnchantment(ItemStack, Holder)} instead.
	 * If you want those mechanisms to be able to apply an enchantment, you will need to add your item to the relevant tag or override that method.
	 *
	 * @param stack       the item stack to be enchanted
	 * @param enchantment the enchantment to be applied
	 * @return true if this item should be treated as a primary item for the enchantment
	 *
	 * @see #supportsEnchantment(ItemStack, Holder)
	 */
	default boolean isPrimaryItemFor(ItemStack stack, Holder<Enchantment> enchantment) {
		if (stack.getItem() == Items.BOOK) {
			return true;
		}
		Optional<HolderSet<Item>> primaryItems = enchantment.value().definition().primaryItems();
		return this.supportsEnchantment(stack, enchantment) && (primaryItems.isEmpty() || stack.is(primaryItems.get()));
	}

	/**
	 * Checks if the provided enchantment is applicable to the passed item stack.
	 * <p>
	 * By default, this checks if the {@link Enchantment.EnchantmentDefinition#supportedItems()} contains this item,
	 * special casing enchanted books as they may receive any enchantment.
	 * <p>
	 * Overriding this method allows for dynamic logic that would not be possible using the tag system.
	 *
	 * @param stack       the item stack to be enchanted
	 * @param enchantment the enchantment to be applied
	 * @return true if this item can accept the enchantment
	 *
	 * @see #isPrimaryItemFor(ItemStack, Holder)
	 */
	default boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
		return stack.is(Items.ENCHANTED_BOOK) || enchantment.value().isSupportedItem(stack);
	}
}
