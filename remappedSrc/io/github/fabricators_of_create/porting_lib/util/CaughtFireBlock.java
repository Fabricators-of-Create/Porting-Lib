package io.github.fabricators_of_create.porting_lib.util;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;


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
	default void onCaughtFire(BlockState state, World level, BlockPos pos, @Nullable Direction direction, @Nullable LivingEntity igniter) {}
}
