package io.github.fabricators_of_create.porting_lib.item.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import io.github.fabricators_of_create.porting_lib.item.extensions.ArmorTextureItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;

public class ItemClientHooks {
	public static ResourceLocation getArmorTexture(Entity entity, ItemStack armor, ArmorMaterial.Layer layer, boolean innerModel, EquipmentSlot slot) {
		return getArmorTexture(entity, armor, layer, innerModel, slot, op -> ((ArmorMaterial.Layer) op[0]).texture((boolean) op[1]));
	}

	public static ResourceLocation getArmorTexture(Entity entity, ItemStack armor, ArmorMaterial.Layer layer, boolean innerModel, EquipmentSlot slot, Operation<ResourceLocation> original) {
		ResourceLocation result = null;
		if (armor.getItem() instanceof ArmorTextureItem armorTextureItem)
			result = armorTextureItem.getArmorTexture(armor, entity, slot, layer, innerModel);
		return result != null ? result : original.call(layer, innerModel);
	}
}
