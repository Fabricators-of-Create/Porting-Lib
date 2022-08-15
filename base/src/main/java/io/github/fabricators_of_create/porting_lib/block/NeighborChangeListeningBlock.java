package io.github.fabricators_of_create.porting_lib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public interface NeighborChangeListeningBlock {
	void onNeighborChange(BlockState state, LevelReader world, BlockPos pos, BlockPos neighbor);
}
