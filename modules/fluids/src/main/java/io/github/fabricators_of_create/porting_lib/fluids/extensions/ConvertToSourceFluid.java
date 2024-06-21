package io.github.fabricators_of_create.porting_lib.fluids.extensions;

import io.github.fabricators_of_create.porting_lib.fluids.mixin.FlowingFluidAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;

/**
 * Must be implemented on {@link net.minecraft.world.level.material.FlowingFluid}
 */
public interface ConvertToSourceFluid {
	default boolean canConvertToSource(FluidState state, Level level, BlockPos pos) {
		return ((FlowingFluidAccessor) this).callCanConvertToSource(level);
	}
}
