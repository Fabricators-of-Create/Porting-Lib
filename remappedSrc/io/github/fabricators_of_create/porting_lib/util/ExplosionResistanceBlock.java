package io.github.fabricators_of_create.porting_lib.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.explosion.Explosion;

public interface ExplosionResistanceBlock {
	/**
	 * Location sensitive version of getExplosionResistance
	 *
	 * @param level The current level
	 * @param pos Block position in level
	 * @param explosion The explosion
	 * @return The amount of the explosion absorbed.
	 */
	default float getExplosionResistance(BlockState state, BlockView level, BlockPos pos, Explosion explosion) {
		return ((Block) this).getBlastResistance();
	}
}
