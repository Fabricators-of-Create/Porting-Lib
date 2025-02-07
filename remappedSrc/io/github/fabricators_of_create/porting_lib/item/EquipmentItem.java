package io.github.fabricators_of_create.porting_lib.item;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public interface EquipmentItem {
	EquipmentSlot getEquipmentSlot(ItemStack stack);
}
