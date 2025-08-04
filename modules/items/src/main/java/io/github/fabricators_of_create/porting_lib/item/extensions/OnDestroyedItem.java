package io.github.fabricators_of_create.porting_lib.item.extensions;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface OnDestroyedItem {
	/**
	 * Called when an item entity for this stack is destroyed. Note: The {@link ItemStack} can be retrieved from the item entity.
	 *
	 * @param itemEntity   The item entity that was destroyed.
	 * @param damageSource Damage source that caused the item entity to "die".
	 */
	default void onDestroyed(ItemEntity itemEntity, DamageSource damageSource) {
		((Item) this).onDestroyed(itemEntity);
	}
}
