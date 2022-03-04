package io.github.fabricators_of_create.porting_lib.extensions;

import io.github.fabricators_of_create.porting_lib.util.ToolAction;
import net.minecraft.world.item.ItemStack;

public interface ItemExtensions {
	default boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
