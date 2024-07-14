package io.github.fabricators_of_create.porting_lib.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public interface DamageableItem {
	/**
	 * Return the itemDamage represented by this ItemStack. Defaults to the Damage
	 * entry in the stack NBT, but can be overridden here for other sources.
	 *
	 * @param stack The itemstack that is damaged
	 * @return the damage value
	 */
	default int getDamage(ItemStack stack) {
		return Mth.clamp(stack.getOrDefault(DataComponents.DAMAGE, 0), 0, stack.getMaxDamage());
	}

	/**
	 * Return the maxDamage for this ItemStack. Defaults to the maxDamage field in
	 * this item, but can be overridden here for other sources such as NBT.
	 *
	 * @param stack The itemstack that is damaged
	 * @return the damage value
	 */
	default int getMaxDamage(ItemStack stack) {
		return stack.getOrDefault(DataComponents.MAX_DAMAGE, 0);
	}

	/**
	 * Set the damage for this itemstack. Note, this method is responsible for zero
	 * checking.
	 *
	 * @param stack  the stack
	 * @param damage the new damage value
	 */
	default void setDamage(ItemStack stack, int damage) {
		stack.set(DataComponents.DAMAGE, Mth.clamp(damage, 0, stack.getMaxDamage()));
	}
}
