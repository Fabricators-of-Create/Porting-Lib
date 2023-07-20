package io.github.fabricators_of_create.porting_lib.fluids.extensions;

import io.github.fabricators_of_create.porting_lib.fluids.FluidType;
import net.minecraft.world.level.material.FluidState;

public interface FluidStateExtension {
	/**
	 * Returns the type of this fluid.
	 *
	 * @return the type of this fluid
	 */
	default FluidType getFluidType() {
		return ((FluidState) this).getType().getFluidType();
	}
}
