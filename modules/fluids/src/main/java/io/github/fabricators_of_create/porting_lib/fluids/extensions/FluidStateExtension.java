package io.github.fabricators_of_create.porting_lib.fluids.extensions;

import io.github.fabricators_of_create.porting_lib.fluids.FluidType;
import io.github.fabricators_of_create.porting_lib.fluids.mixin.FlowingFluidAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
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

	/**
	 * Returns whether the fluid can create a source.
	 *
	 * @param level the level that can get the fluid
	 * @param pos   the location of the fluid
	 * @return {@code true} if the fluid can create a source, {@code false} otherwise
	 */
	default boolean port_lib$canConvertToSource(Level level, BlockPos pos) {
		Fluid fluid = ((FluidState) this).getType();
		if (fluid instanceof ConvertToSourceFluid sourceFluid)
			return sourceFluid.canConvertToSource((FluidState) this, level, pos);
		if (fluid instanceof FlowingFluid flowingFluid)
			return ((FlowingFluidAccessor) flowingFluid).callCanConvertToSource(level);
		return false;
	}
}
