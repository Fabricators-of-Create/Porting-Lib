package io.github.fabricators_of_create.porting_lib.transfer.fluid;

import javax.annotation.Nullable;

import net.minecraft.core.Direction;

public interface FluidTransferable {
	@Nullable
	IFluidHandler getFluidHandler(@Nullable Direction direction);
}
