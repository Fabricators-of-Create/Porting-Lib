package io.github.fabricators_of_create.porting_lib.transfer.fluid;

import javax.annotation.Nonnull;

public class EmptyFluidHandler implements IFluidHandler {
	public static final EmptyFluidHandler INSTANCE = new EmptyFluidHandler();

	protected EmptyFluidHandler() {
	}

	@Override
	public int getTanks() {
		return 1;
	}

	@Nonnull
	@Override
	public FluidStack getFluidInTank(int tank) {
		return FluidStack.EMPTY;
	}

	@Override
	public long getTankCapacity(int tank) {
		return 0;
	}

	@Override
	public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
		return true;
	}

	@Override
	public long fill(FluidStack resource, boolean sim) {
		return 0;
	}

	@Nonnull
	@Override
	public FluidStack drain(FluidStack resource, boolean sim) {
		return FluidStack.EMPTY;
	}

	@Nonnull
	@Override
	public FluidStack drain(long maxDrain, boolean sim) {
		return FluidStack.EMPTY;
	}
}
