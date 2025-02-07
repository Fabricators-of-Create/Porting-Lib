package io.github.fabricators_of_create.porting_lib.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public interface NeighborChangeListeningBlock {
	void onNeighborChange(BlockState state, WorldView world, BlockPos pos, BlockPos neighbor);
}
