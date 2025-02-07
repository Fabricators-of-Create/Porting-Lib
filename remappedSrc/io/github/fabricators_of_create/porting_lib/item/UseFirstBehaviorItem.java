package io.github.fabricators_of_create.porting_lib.item;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;

public interface UseFirstBehaviorItem {
	ActionResult onItemUseFirst(ItemStack stack, ItemUsageContext context);
}
