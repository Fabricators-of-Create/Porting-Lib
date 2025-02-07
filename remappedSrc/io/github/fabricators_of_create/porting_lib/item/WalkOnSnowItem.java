package io.github.fabricators_of_create.porting_lib.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public interface WalkOnSnowItem {
	/**
	 * Called by the powdered snow block to check if a living entity wearing this can walk on the snow, granting the same behavior as leather boots.
	 * Only affects items worn in the boots slot.
	 *
	 * @param stack  Stack instance
	 * @param wearer The entity wearing this ItemStack
	 *
	 * @return True if the entity can walk on powdered snow
	 */
	default boolean canWalkOnPowderedSnow(ItemStack stack, LivingEntity wearer) {
		return stack.isOf(Items.LEATHER_BOOTS);
	}
}
