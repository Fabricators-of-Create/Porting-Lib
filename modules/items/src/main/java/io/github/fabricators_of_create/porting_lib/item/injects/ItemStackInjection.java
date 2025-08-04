package io.github.fabricators_of_create.porting_lib.item.injects;

import io.github.fabricators_of_create.porting_lib.item.extensions.DamageableItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;

public interface ItemStackInjection {
	/**
	 * Determines if an item is reparable, used by Repair recipes and Grindstone.
	 *
	 * @return True if reparable
	 */
	default boolean isRepairable() {
		var stack = (ItemStack) this;
		if (stack.getItem() instanceof DamageableItem repairableItem) {
			return repairableItem.isRepairable(stack);
		}
		return ((ItemInjection) stack.getItem()).port_lib$canRepair() && stack.has(DataComponents.MAX_DAMAGE);
	}
}
