package io.github.fabricators_of_create.porting_lib.blocks.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public interface LightEmissiveBlock {
	// Due to vanilla no longer passing in level and pos some mods that call LightEngine#hasDifferentLightProperties will result in the level and pos being null
	/**
	 * Get a light value for this block, taking into account the given state and coordinates, normal ranges are between 0 and 15
	 *
	 * @param state The state of this block
	 * @param level The level this block is in, may be {@link EmptyBlockGetter#INSTANCE}, see implementation notes
	 * @param pos   The position of this block in the level, may be {@link BlockPos#ZERO}, see implementation notes
	 * @return The light value
	 */
	default int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
		return state.getLightEmission();
	}
}
