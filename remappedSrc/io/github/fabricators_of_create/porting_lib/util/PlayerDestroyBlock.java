package io.github.fabricators_of_create.porting_lib.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

// TODO: Fully implement
public interface PlayerDestroyBlock {
	/**
	 * Called when a player removes a block.  This is responsible for
	 * actually destroying the block, and the block is intact at time of call.
	 * This is called regardless of whether the player can harvest the block or
	 * not.
	 *
	 * Return true if the block is actually destroyed.
	 *
	 * Note: When used in multiplayer, this is called on both client and
	 * server sides!
	 *
	 * @param state The current state.
	 * @param level The current level
	 * @param player The player damaging the block, may be null
	 * @param pos Block position in level
	 * @param willHarvest True if Block.harvestBlock will be called after this, if the return in true.
	 *        Can be useful to delay the destruction of tile entities till after harvestBlock
	 * @param fluid The current fluid state at current position
	 * @return True if the block is actually destroyed.
	 */
	default boolean onDestroyedByPlayer(BlockState state, World level, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid) {
		((Block) this).onBreak(level, pos, state, player);
		return level.setBlockState(pos, fluid.getBlockState(), level.isClient ? 11 : 3);
	}
}
