package io.github.fabricators_of_create.porting_lib.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;

public interface ShieldBlockItem {
	/**
	 * Can this Item disable a shield
	 *
	 * @param stack    The ItemStack
	 * @param shield   The shield in question
	 * @param entity   The LivingEntity holding the shield
	 * @param attacker The LivingEntity holding the ItemStack
	 * @return True if this ItemStack can disable the shield in question.
	 */
	default boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
		return this instanceof AxeItem;
	}
}
