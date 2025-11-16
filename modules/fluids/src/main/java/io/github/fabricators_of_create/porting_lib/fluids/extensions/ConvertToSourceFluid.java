package io.github.fabricators_of_create.porting_lib.fluids.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;

public interface ConvertToSourceFluid {
	/**
	 * Returns whether the fluid can create a source.
	 *
	 * @param state the state of the fluid
	 * @param level the level that can get the fluid
	 * @param pos the location of the fluid
	 * @return {@code true} if the fluid can create a source, {@code false} otherwise
	 */
	default boolean canConvertToSource(FluidState state, Level level, BlockPos pos) {
		return false;
	}
}
