package io.github.fabricators_of_create.porting_lib.block;

import io.github.fabricators_of_create.porting_lib.util.PortingHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface HarvestableBlock {
	/**
	 * Determines if the player can harvest this block, obtaining it's drops when the block is destroyed.
	 *
	 * @param level The current level
	 * @param pos The block's current position
	 * @param player The player damaging the block
	 * @return True to spawn the drops
	 */
	default boolean canHarvestBlock(BlockState state, BlockGetter level, BlockPos pos, Player player) {
		return PortingHooks.isCorrectToolForDrops(state, player);
	}
}
