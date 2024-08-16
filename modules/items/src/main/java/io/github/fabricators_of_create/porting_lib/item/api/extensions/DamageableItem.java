package io.github.fabricators_of_create.porting_lib.item.api.extensions;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;

public interface DamageableItem {
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
