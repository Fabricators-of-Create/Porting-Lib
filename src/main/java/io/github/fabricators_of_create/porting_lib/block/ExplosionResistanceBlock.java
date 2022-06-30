package io.github.fabricators_of_create.porting_lib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public interface ExplosionResistanceBlock {
	/**
	 * Location sensitive version of getExplosionResistance
	 *
	 * @param level The current level
	 * @param pos Block position in level
	 * @param explosion The explosion
	 * @return The amount of the explosion absorbed.
	 */
	default float getExplosionResistance(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion) {
		return ((Block) this).getExplosionResistance();
	}
}
