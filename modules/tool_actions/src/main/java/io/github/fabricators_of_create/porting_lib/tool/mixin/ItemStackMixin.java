package io.github.fabricators_of_create.porting_lib.tool.mixin;

import io.github.fabricators_of_create.porting_lib.tool.ToolAction;
import io.github.fabricators_of_create.porting_lib.tool.addons.ToolActionItem;
import io.github.fabricators_of_create.porting_lib.tool.extensions.ItemStackExtensions;
import io.github.fabricators_of_create.porting_lib.tool.extensions.VanillaToolActionItem;
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
		var item = getItem();
		if (item instanceof ToolActionItem toolActionItem)
			return toolActionItem.canPerformAction((ItemStack) (Object) this, toolAction);
		if (item instanceof VanillaToolActionItem toolActionItem)
			return toolActionItem.port_lib$canPerformAction((ItemStack) (Object) this, toolAction);
		return false;
	}
}
