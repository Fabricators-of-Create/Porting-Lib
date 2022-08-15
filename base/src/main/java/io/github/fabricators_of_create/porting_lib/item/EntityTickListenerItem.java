package io.github.fabricators_of_create.porting_lib.item;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public interface EntityTickListenerItem {
	boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity);
}
