package io.github.fabricators_of_create.porting_lib.fluids.extensions;

import io.github.fabricators_of_create.porting_lib.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

public interface FluidExtension {
	@Nullable
	default FluidType getFluidType() {
		throw new RuntimeException();
	}
}
