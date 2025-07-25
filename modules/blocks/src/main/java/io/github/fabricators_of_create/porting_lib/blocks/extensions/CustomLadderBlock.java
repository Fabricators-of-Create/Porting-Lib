package io.github.fabricators_of_create.porting_lib.blocks.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public interface CustomLadderBlock {
	/**
	 * Checks if a player or entity can use this block to 'climb' like a ladder.
	 *
	 * @param state  The current state
	 * @param level  The current level
	 * @param pos    Block position in level
	 * @param entity The entity trying to use the ladder, CAN be null.
	 * @return True if the block should act like a ladder
	 */
	default boolean isLadder(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity) {
		return state.is(BlockTags.CLIMBABLE);
	}
}
