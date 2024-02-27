package io.github.fabricators_of_create.porting_lib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public interface FireSourceBlock {
	/**
	 * Currently only called by fire when it is on top of this block.
	 * Returning true will prevent the fire from naturally dying during updating.
	 * Also prevents firing from dying from rain.
	 *
	 * @param state The current state
	 * @param level The current level
	 * @param pos Block position in level
	 * @param direction The direction that the fire is coming from
	 * @return True if this block sustains fire, meaning it will never go out.
	 */
	default boolean isFireSource(BlockState state, LevelReader level, BlockPos pos, Direction direction) {
		return state.is(level.dimensionType().infiniburn());
	}
}
