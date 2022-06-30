package io.github.fabricators_of_create.porting_lib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

// TODO: Fully implement
public interface CaughtFireBlock {
	/**
	 * If the block is flammable, this is called when it gets lit on fire.
	 *
	 * @param state The current state
	 * @param level The current level
	 * @param pos Block position in level
	 * @param direction The direction that the fire is coming from
	 * @param igniter The entity that lit the fire
	 */
	default void onCaughtFire(BlockState state, Level level, BlockPos pos, @Nullable Direction direction, @Nullable LivingEntity igniter) {}
}
