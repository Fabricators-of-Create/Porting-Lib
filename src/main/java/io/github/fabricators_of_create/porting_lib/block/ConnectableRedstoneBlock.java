package io.github.fabricators_of_create.porting_lib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface ConnectableRedstoneBlock {
	boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side);
}
