package io.github.fabricators_of_create.porting_lib.blocks.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public interface PlayerDestroyBlock {
	/**
	 * Called when a player removes a block. This is responsible for
	 * actually destroying the block, and the block is intact at time of call.
	 * This is called regardless of whether the player can harvest the block or
	 * not.
	 *
	 * Return true if the block is actually destroyed.
	 *
	 * This function is called on both the logical client and logical server.
	 *
	 * @param state       The current state.
	 * @param level       The current level
	 * @param player      The player damaging the block, may be null
	 * @param pos         Block position in level
	 * @param willHarvest The result of {@link HarvestableBlock#canHarvestBlock}, if called on the server by a non-creative player, otherwise always false.
	 * @param fluid       The current fluid state at current position
	 * @return True if the block is actually destroyed.
	 */
	default boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
		if (level.isClientSide()) {
			// On the client, vanilla calls Level#setBlock, per MultiPlayerGameMode#destroyBlock
			return level.setBlock(pos, fluid.createLegacyBlock(), 11);
		} else {
			// On the server, vanilla calls Level#removeBlock, per ServerPlayerGameMode#destroyBlock
			return level.removeBlock(pos, false);
		}
	}
}
