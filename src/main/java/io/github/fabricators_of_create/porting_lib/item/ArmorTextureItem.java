package io.github.fabricators_of_create.porting_lib.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

/**
 * An Armor Item with custom logic for getting the texture.
 */
public interface ArmorTextureItem {
	String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type);
}
