package io.github.fabricators_of_create.porting_lib.fluids.extensions;

import io.github.fabricators_of_create.porting_lib.fluids.FluidType;
import net.minecraft.world.level.material.FluidState;

import org.jetbrains.annotations.Nullable;

public interface FluidStateExtension {
	/**
	 * Returns the type of this fluid.
	 *
	 * @return the type of this fluid
	 */
	@Nullable
	default FluidType getFluidType() {
		return ((FluidState) this).getType().getFluidType();
	}
}
