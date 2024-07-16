package io.github.fabricators_of_create.porting_lib.tool.extensions;

import io.github.fabricators_of_create.porting_lib.tool.ItemAbility;
import net.minecraft.world.item.ItemStack;

public interface VanillaToolActionItem {
	boolean port_lib$canPerformAction(ItemStack stack, ItemAbility toolAction);
}
