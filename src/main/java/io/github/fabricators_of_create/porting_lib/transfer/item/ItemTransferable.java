package io.github.fabricators_of_create.porting_lib.transfer.item;

import javax.annotation.Nullable;

import io.github.fabricators_of_create.porting_lib.util.LazyOptional;
import net.minecraft.core.Direction;

public interface ItemTransferable {
	LazyOptional<IItemHandler> getItemHandler(@Nullable Direction direction);
}
