package io.github.fabricators_of_create.porting_lib.extensions;

import io.github.fabricators_of_create.porting_lib.util.ToolAction;
import net.fabricmc.fabric.impl.item.ItemExtensions;
import net.minecraft.world.item.ItemStack;

public interface ItemStackExtensions {
	boolean canPerformAction(ToolAction toolAction);
}
