package io.github.fabricators_of_create.porting_lib.transfer.fluid;

import javax.annotation.Nullable;

import io.github.fabricators_of_create.porting_lib.util.LazyOptional;
import net.minecraft.core.Direction;

public interface FluidTransferable {
	LazyOptional<IFluidHandler> getFluidHandler(@Nullable Direction direction);

	default boolean shouldRunClientSide() {
		return true;
	}
}
