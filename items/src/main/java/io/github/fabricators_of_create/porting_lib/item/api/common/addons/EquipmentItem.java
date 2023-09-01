package io.github.fabricators_of_create.porting_lib.item.api.common.addons;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public interface EquipmentItem {
	EquipmentSlot getEquipmentSlot(ItemStack stack);
}
