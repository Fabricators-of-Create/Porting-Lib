package io.github.fabricators_of_create.porting_lib.blocks.extensions;

import io.github.fabricators_of_create.porting_lib.blocks.mixin.common.FireBlockAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public interface FlammableBlock {
	// These methods are here because fabric doesn't expose their registry and I can't be assed to support it, forge allows for more customization anyway
	/**
	 * Chance that fire will spread and consume this block.
	 * 300 being a 100% chance, 0, being a 0% chance.
	 *
	 * @param state     The current state
	 * @param level     The current level
	 * @param pos       Block position in level
	 * @param direction The direction that the fire is coming from
	 * @return A number ranging from 0 to 300 relating used to determine if the block will be consumed by fire
	 */
	default int getFlammability(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
		return ((FireBlockAccessor) Blocks.FIRE).callGetBurnOdds(state);
	}

	/**
	 * Called when fire is updating on a neighbor block.
	 * The higher the number returned, the faster fire will spread around this block.
	 *
	 * @param state     The current state
	 * @param level     The current level
	 * @param pos       Block position in level
	 * @param direction The direction that the fire is coming from
	 * @return A number that is used to determine the speed of fire growth around the block
	 */
	default int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return ((FireBlockAccessor) Blocks.FIRE).callGetIgniteOdds(state);
	}

	/**
	 * Called when fire is updating, checks if a block face can catch fire.
	 *
	 *
	 * @param state     The current state
	 * @param level     The current level
	 * @param pos       Block position in level
	 * @param direction The direction that the fire is coming from
	 * @return True if the face can be on fire, false otherwise.
	 */
	default boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		if (state.getBlock() instanceof FlammableBlock block) {
			return block.getFlammability(state, level, pos, direction) > 0;
		}
		return ((FireBlockAccessor) Blocks.FIRE).callGetBurnOdds(state) > 0;
	}

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

	/**
	 * Helper method for mods which also handles vanilla
	 */
	static void onCaughtFireVanilla(BlockState state, Level level, BlockPos pos, @Nullable Direction direction, @Nullable LivingEntity igniter) {
		if (state.getBlock() instanceof FlammableBlock block) {
			block.onCaughtFire(state, level, pos, direction, igniter);
		} else if (state.getBlock() == Blocks.TNT) {
			TntBlock.explode(level, pos);
		}
	}
}
