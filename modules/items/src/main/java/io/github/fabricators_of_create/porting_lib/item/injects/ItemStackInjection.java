package io.github.fabricators_of_create.porting_lib.item.injects;

import io.github.fabricators_of_create.porting_lib.item.extensions.DamageableItem;
import net.minecraft.world.item.ItemStack;

public interface ItemStackInjection {
	/**
	 * Determines if an item is reparable, used by Repair recipes and Grindstone.
	 *
	 * @return True if reparable
	 */
	default boolean isRepairable() {
		var stack = (ItemStack) (Object) this;
		if (stack.getItem() instanceof DamageableItem repairableItem) {
			return repairableItem.isRepairable(stack);
		}
		return true;/*stack.getItem().canRepair && isDamageable(stack);*/
	}
}
