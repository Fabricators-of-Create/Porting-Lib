package io.github.fabricators_of_create.porting_lib.item.extensions;

import io.github.fabricators_of_create.porting_lib.item.injects.ItemInjection;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;

public interface DamageableItem {

	/**
	 * Called by CraftingManager to determine if an item is repairable.
	 *
	 * @return True if reparable
	 */
	default boolean isRepairable(ItemStack stack) {
		return ((ItemInjection) this).port_lib$canRepair() && isDamageable(stack);
	}

	/**
	 * Used to test if this item can be damaged, but with the ItemStack in question.
	 * Please note that in some cases no ItemStack is available, so the stack-less method will be used.
	 *
	 * @param stack ItemStack in the Chest slot of the entity.
	 */
	default boolean isDamageable(ItemStack stack) {
		return stack.has(DataComponents.MAX_DAMAGE);
	}
}
