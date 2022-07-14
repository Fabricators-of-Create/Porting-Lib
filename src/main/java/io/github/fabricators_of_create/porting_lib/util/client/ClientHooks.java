package io.github.fabricators_of_create.porting_lib.util.client;

import io.github.fabricators_of_create.porting_lib.util.ArmorTextureItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class ClientHooks {
	public static String getArmorTexture(Entity entity, ItemStack armor, String _default, EquipmentSlot slot, String type) {
		String result = null;
		if (armor.getItem() instanceof ArmorTextureItem armorTextureItem)
			result = armorTextureItem.getArmorTexture(armor, entity, slot, type);
		return result != null ? result : _default;
	}
}
