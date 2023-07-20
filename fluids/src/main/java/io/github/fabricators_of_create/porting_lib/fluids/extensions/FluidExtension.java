package io.github.fabricators_of_create.porting_lib.fluids.extensions;

import io.github.fabricators_of_create.porting_lib.fluids.FluidType;

public interface FluidExtension {
	default FluidType getFluidType() {
		throw new RuntimeException();
	}
}
