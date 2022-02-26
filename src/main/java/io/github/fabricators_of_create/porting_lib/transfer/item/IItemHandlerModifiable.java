package io.github.fabricators_of_create.porting_lib.transfer.item;

import net.minecraft.world.item.ItemStack;

public interface IItemHandlerModifiable extends IItemHandler {
	void setStackInSlot(int slot, ItemStack stack);
}
