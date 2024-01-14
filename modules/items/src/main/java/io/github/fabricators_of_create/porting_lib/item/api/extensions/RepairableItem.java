package io.github.fabricators_of_create.porting_lib.item.api.extensions;

import net.minecraft.world.item.ItemStack;

// TODO: Implement this on GrindstoneMenu
public interface RepairableItem {
	/**
	 * Called by CraftingManager to determine if an item is reparable.
	 *
	 * @return True if reparable
	 */
	boolean isRepairable(ItemStack stack);
}
