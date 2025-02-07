package io.github.fabricators_of_create.porting_lib.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public interface LightEmissiveBlock {
	default int getLightEmission(BlockState state, BlockView world, BlockPos pos) {
		return state.getLuminance();
	}
}
