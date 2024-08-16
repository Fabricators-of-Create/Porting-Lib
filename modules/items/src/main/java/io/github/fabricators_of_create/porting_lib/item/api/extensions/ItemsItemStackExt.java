package io.github.fabricators_of_create.porting_lib.item.api.extensions;

import net.minecraft.world.item.ItemStack;

public interface ItemsItemStackExt {
	/**
	 * Determines if an item is reparable, used by Repair recipes and Grindstone.
	 *
	 * @return True if reparable
	 */
	default boolean isRepairable() {
		var stack = (ItemStack) this;
		if (stack.getItem() instanceof RepairableItem repairableItem) {
			return repairableItem.isRepairable(stack);
		}
		return true;/*stack.getItem().canRepair && isDamageable(stack);*/
	}
}
