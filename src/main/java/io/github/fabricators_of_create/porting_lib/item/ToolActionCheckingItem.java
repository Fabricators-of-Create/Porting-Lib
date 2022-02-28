package io.github.fabricators_of_create.porting_lib.item;

import io.github.fabricators_of_create.porting_lib.util.ToolAction;
import net.minecraft.world.item.ItemStack;

public interface ToolActionCheckingItem {
	boolean canPerformAction(ItemStack stack, ToolAction toolAction);
}
