package io.github.fabricators_of_create.porting_lib.event.common;

import com.google.common.collect.Multimap;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

public interface ItemAttributeModifierCallback {
	Event<ItemAttributeModifierCallback> EVENT = EventFactory.createArrayBacked(ItemAttributeModifierCallback.class, callbacks -> (stack, slotType, modifiers) -> {
		for (ItemAttributeModifierCallback e : callbacks)
			e.onItemStackModifiers(stack, slotType, modifiers);
	});

	void onItemStackModifiers(ItemStack stack, EquipmentSlot slotType, Multimap<Attribute, AttributeModifier> modifiers);
}
