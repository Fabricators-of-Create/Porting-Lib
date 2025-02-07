package io.github.fabricators_of_create.porting_lib.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public interface ArmorTextureItem {
	String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type);
}
