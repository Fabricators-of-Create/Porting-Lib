package io.github.fabricators_of_create.porting_lib.tool.addons;

import io.github.fabricators_of_create.porting_lib.tool.ToolAction;
import io.github.fabricators_of_create.porting_lib.tool.ToolActions;
import net.minecraft.world.item.ItemStack;

public interface ToolActionItem {
	/**
	 * Queries if an item can perform the given action.
	 * See {@link ToolActions} for a description of each stock action
	 * @param stack The stack being used
	 * @param toolAction The action being queried
	 * @return True if the stack can perform the action
	 */
	default boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
		return false;
	}
}
