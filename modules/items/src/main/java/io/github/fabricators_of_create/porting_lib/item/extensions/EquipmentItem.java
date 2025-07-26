package io.github.fabricators_of_create.porting_lib.item.extensions;

import net.fabricmc.fabric.api.item.v1.EquipmentSlotProvider;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface EquipmentItem {
	/**
	 * Override this to set a non-default armor slot for an ItemStack, but <em>do
	 * not use this to get the armor slot of said stack; for that, use
	 * {@link LivingEntity#getEquipmentSlotForItem(ItemStack)}..</em>
	 *
	 * @param stack the ItemStack
	 * @return the armor slot of the ItemStack, or {@code null} to let the default
	 *         vanilla logic as per {@code LivingEntity.getSlotForItemStack(stack)}
	 *         decide
	 * @apiNote Try to use {@link FabricItem.Settings#equipmentSlot(EquipmentSlotProvider)} for acquiring an equipment slot instead,
	 * 			as it also provides entity information.
	 */
	default EquipmentSlot getEquipmentSlot(ItemStack stack) {
		return null;
	}

	/**
	 * Determines if the specific ItemStack can be placed in the specified armor
	 * slot, for the entity.
	 *
	 * @param stack     The ItemStack
	 * @param armorType Armor slot to be verified.
	 * @param entity    The entity trying to equip the armor
	 * @return True if the given ItemStack can be inserted in the slot
	 */
	default boolean canEquip(ItemStack stack, EquipmentSlot armorType, LivingEntity entity) {
		return entity.getEquipmentSlotForItem(stack) == armorType;
	}
}
