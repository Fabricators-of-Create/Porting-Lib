package io.github.fabricators_of_create.porting_lib.extensions;

import io.github.fabricators_of_create.porting_lib.util.ToolAction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public interface ItemExtensions {
	default Supplier<Item> getSupplier() {
		return () -> new Item(new Item.Properties());
	}

	default boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return !oldStack.equals(newStack);
	}

  default boolean canPerformAction(ItemStack stack, ToolAction toolAction)
  {
    return false;
  }
}
