package io.github.fabricators_of_create.porting_lib.enchant;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

/**
 * An enchantment that can be added to CreativeModeTabs via enchanted books. This also determines if the Enchantment
 * should be present in the {@link CreativeModeTab#TAB_SEARCH search tab}. The desired tab's
 * {@link CreativeModeTab#getEnchantmentCategories() enchantment categories} must also not be empty.
 */
public interface CreativeModeTabPresentEnchantment {
	boolean allowedInCreativeTab(Item book, CreativeModeTab tab);
}
