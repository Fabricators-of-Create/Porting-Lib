package io.github.fabricators_of_create.porting_lib.blocks.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface WeakPowerCheckingBlock {
	/**
	 * Called to determine whether to allow the block to handle its own indirect power rather than using the default rules.
	 *
	 * @param level The level
	 * @param pos   Block position in level
	 * @param side  The INPUT side of the block to be powered - ie the opposite of this block's output side
	 * @return Whether Block#isProvidingWeakPower should be called when determining indirect power
	 */
	default boolean shouldCheckWeakPower(BlockState state, SignalGetter level, BlockPos pos, Direction side) {
		return state.isRedstoneConductor(level, pos);
	}
}
