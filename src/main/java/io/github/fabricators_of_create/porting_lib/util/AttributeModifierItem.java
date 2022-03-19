package io.github.fabricators_of_create.porting_lib.util;

import com.google.common.collect.Multimap;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface AttributeModifierItem {
	/**
	 * ItemStack sensitive version of getItemAttributeModifiers
	 */
	default Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
		return ((Item) this).getDefaultAttributeModifiers(slot);
	}
}
