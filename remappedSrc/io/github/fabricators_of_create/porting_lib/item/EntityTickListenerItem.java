package io.github.fabricators_of_create.porting_lib.item;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;

public interface EntityTickListenerItem {
	boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity);
}
