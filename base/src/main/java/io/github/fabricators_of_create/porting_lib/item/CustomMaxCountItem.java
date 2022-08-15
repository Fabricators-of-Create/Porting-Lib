package io.github.fabricators_of_create.porting_lib.item;

import net.minecraft.world.item.ItemStack;

public interface CustomMaxCountItem {
	int getItemStackLimit(ItemStack stack);
}
