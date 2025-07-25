package io.github.fabricators_of_create.porting_lib.blocks.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public interface CustomDisplayFluidOverlayBlock {
	/**
	 * Called to determine whether this block should use the fluid overlay texture or flowing texture when it is placed under the fluid.
	 *
	 * @param state      The current state
	 * @param level      The level
	 * @param pos        Block position in level
	 * @param fluidState The state of the fluid
	 * @return Whether the fluid overlay texture should be used
	 */
	default boolean shouldDisplayFluidOverlay(BlockState state, BlockAndTintGetter level, BlockPos pos, FluidState fluidState) {
		return state.getBlock() instanceof HalfTransparentBlock || state.getBlock() instanceof LeavesBlock;
	}
}
