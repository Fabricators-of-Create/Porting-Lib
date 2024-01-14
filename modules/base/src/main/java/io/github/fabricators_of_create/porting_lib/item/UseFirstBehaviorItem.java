package io.github.fabricators_of_create.porting_lib.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;

public interface UseFirstBehaviorItem {
	/**
	 * This is called when the item is used, before the block is activated.
	 *
	 * @return Return PASS to allow vanilla handling, any other to skip normal code.
	 */
	default InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
		return InteractionResult.PASS;
	}
}
