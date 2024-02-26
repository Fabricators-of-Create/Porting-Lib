package io.github.fabricators_of_create.porting_lib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ObserverBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;

public interface ConnectableRedstoneBlock {
	/**
	 * Whether redstone dust should visually connect to this block on a given side
	 * <p>
	 * The default implementation is identical to
	 * {@code RedStoneWireBlock#shouldConnectTo(BlockState, Direction)}
	 *
	 * <p>
	 * {@link RedStoneWireBlock} updates its visual connection when
	 * {@link BlockState#updateShape(Direction, BlockState, LevelAccessor, BlockPos, BlockPos)}
	 * is called, this callback is used during the evaluation of its new shape.
	 *
	 * @param state     The current state
	 * @param level     The level
	 * @param pos       The block position in level
	 * @param direction The coming direction of the redstone dust connection (with respect to the block at pos)
	 * @return True if redstone dust should visually connect on the side passed
	 * <p>
	 * If the return value is evaluated based on level and pos (e.g. from BlockEntity), then the implementation of
	 * this block should notify its neighbors to update their shapes when necessary. Consider using
	 * {@link BlockState#updateNeighbourShapes(LevelAccessor, BlockPos, int, int)} or
	 * {@link BlockState#updateShape(Direction, BlockState, LevelAccessor, BlockPos, BlockPos)}.
	 * <p>
	 * Example:
	 * <p>
	 * 1. {@code yourBlockState.updateNeighbourShapes(level, yourBlockPos, UPDATE_ALL);}
	 * <p>
	 * 2. {@code neighborState.updateShape(fromDirection, stateOfYourBlock, level, neighborBlockPos, yourBlockPos)},
	 * where {@code fromDirection} is defined from the neighbor block's point of view.
	 */
	default boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
		if (state.is(Blocks.REDSTONE_WIRE)) {
			return true;
		} else if (state.is(Blocks.REPEATER)) {
			Direction facing = state.getValue(RepeaterBlock.FACING);
			return facing == direction || facing.getOpposite() == direction;
		} else if (state.is(Blocks.OBSERVER)) {
			return direction == state.getValue(ObserverBlock.FACING);
		} else {
			return state.isSignalSource() && direction != null;
		}
	}
}
