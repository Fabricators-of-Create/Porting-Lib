package io.github.fabricators_of_create.porting_lib.blocks.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public interface OnExplodedBlock {
	/**
	 * Called when the block is destroyed by an explosion.
	 * Useful for allowing the block to take into account tile entities,
	 * state, etc. when exploded, before it is removed.
	 *
	 * @param level     The current level
	 * @param pos       Block position in level
	 * @param explosion The explosion instance affecting the block
	 */
	default void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
		level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
		((Block) this).wasExploded(level, pos, explosion);
	}
}
