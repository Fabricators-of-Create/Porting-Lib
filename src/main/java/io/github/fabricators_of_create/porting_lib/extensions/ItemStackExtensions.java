package io.github.fabricators_of_create.porting_lib.extensions;

import io.github.fabricators_of_create.porting_lib.util.ToolAction;
import net.fabricmc.fabric.impl.item.ItemExtensions;
import net.minecraft.world.item.ItemStack;

public interface ItemStackExtensions {
	default boolean canPerformAction(ToolAction toolAction) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
