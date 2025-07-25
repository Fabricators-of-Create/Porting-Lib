package io.github.fabricators_of_create.porting_lib.blocks.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;

public interface CollisionExtendsVerticallyBlock {
	/**
	 * Determines if this block's collision box should be treated as though it can extend above its block space.
	 * Use this to replicate fence and wall behavior.
	 */
	default boolean collisionExtendsVertically(BlockState state, BlockGetter level, BlockPos pos, Entity collidingEntity) {
		return state.is(BlockTags.FENCES) || state.is(BlockTags.WALLS) || this instanceof FenceGateBlock;
	}
}
