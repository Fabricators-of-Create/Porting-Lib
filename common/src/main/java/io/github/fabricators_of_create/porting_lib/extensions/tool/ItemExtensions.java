package io.github.fabricators_of_create.porting_lib.extensions.tool;

import io.github.fabricators_of_create.porting_lib.util.ToolAction;
import net.minecraft.world.item.ItemStack;

public interface ItemExtensions {
	default boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
