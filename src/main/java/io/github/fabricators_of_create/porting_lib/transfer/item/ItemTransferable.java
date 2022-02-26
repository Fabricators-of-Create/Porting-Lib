package io.github.fabricators_of_create.porting_lib.transfer.item;

import javax.annotation.Nullable;

import net.minecraft.core.Direction;

public interface ItemTransferable {
	@Nullable
	IItemHandler getItemHandler(@Nullable Direction direction);
}
