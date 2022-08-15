package io.github.fabricators_of_create.porting_lib.mixin.common.tool;

import io.github.fabricators_of_create.porting_lib.extensions.tool.ItemExtensions;
import io.github.fabricators_of_create.porting_lib.util.ToolAction;
import net.minecraft.world.item.ItemStack;

public class ItemMixin implements ItemExtensions {
	@Override
	public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
		return false;
	}
}
