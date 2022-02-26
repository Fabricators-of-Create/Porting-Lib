package io.github.fabricators_of_create.porting_lib.transfer.fluid;

import net.minecraft.world.item.ItemStack;

public interface IFluidHandlerItem extends IFluidHandler {
	ItemStack getContainer();
}
