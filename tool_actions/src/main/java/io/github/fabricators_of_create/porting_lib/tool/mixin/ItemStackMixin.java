package io.github.fabricators_of_create.porting_lib.tool.mixin;

import io.github.fabricators_of_create.porting_lib.tool.ToolAction;
import io.github.fabricators_of_create.porting_lib.tool.extensions.ItemStackExtensions;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ItemStackExtensions {
	@Shadow
	public abstract Item getItem();

	@Override
	public boolean canPerformAction(ToolAction toolAction) {
		return getItem().canPerformAction((ItemStack) (Object) this, toolAction);
	}
}
