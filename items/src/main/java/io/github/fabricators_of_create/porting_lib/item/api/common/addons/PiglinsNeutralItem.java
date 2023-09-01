package io.github.fabricators_of_create.porting_lib.item.api.common.addons;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;

public interface PiglinsNeutralItem {
	/**
	 * Called by Piglins to check if a given item prevents hostility on sight. If this is true the Piglins will be neutral to the entity wearing this item, and will not
	 * attack on sight. Note: This does not prevent Piglins from becoming hostile due to other actions, nor does it make Piglins that are already hostile stop being so.
	 *
	 * @param wearer The entity wearing this ItemStack
	 *
	 * @return True if piglins are neutral to players wearing this item in an armor slot
	 */
	default boolean makesPiglinsNeutral(ItemStack stack, LivingEntity wearer) {
		return stack.getItem() instanceof ArmorItem && ((ArmorItem) stack.getItem()).getMaterial() == ArmorMaterials.GOLD;
	}
}
