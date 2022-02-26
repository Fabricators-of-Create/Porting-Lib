package io.github.fabricators_of_create.porting_lib.item;

import net.minecraft.world.item.ItemStack;

public interface ReequipAnimationItem {
	boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged);
}
