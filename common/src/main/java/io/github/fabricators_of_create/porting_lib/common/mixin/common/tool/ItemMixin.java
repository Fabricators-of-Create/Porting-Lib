package io.github.fabricators_of_create.porting_lib.common.mixin.common.tool;

import io.github.fabricators_of_create.porting_lib.common.extensions.tool.ItemExtensions;
import io.github.fabricators_of_create.porting_lib.common.util.ToolAction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(Item.class)
public class ItemMixin implements ItemExtensions {
	@Override
	public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
		return false;
	}
}
