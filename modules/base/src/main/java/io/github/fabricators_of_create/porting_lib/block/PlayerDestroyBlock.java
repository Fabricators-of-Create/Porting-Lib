package io.github.fabricators_of_create.porting_lib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

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
	default boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
		((Block) this).playerWillDestroy(level, pos, state, player);
		return level.setBlock(pos, fluid.createLegacyBlock(), level.isClientSide ? 11 : 3);
	}
}
