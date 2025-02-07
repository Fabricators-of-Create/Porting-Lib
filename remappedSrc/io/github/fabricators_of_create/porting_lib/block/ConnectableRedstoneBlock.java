package io.github.fabricators_of_create.porting_lib.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public interface ConnectableRedstoneBlock {
	boolean canConnectRedstone(BlockState state, BlockView world, BlockPos pos, Direction side);
}
