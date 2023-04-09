package io.github.fabricators_of_create.porting_lib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;

public interface SlopeCreationCheckingRailBlock {
	boolean canMakeSlopes(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos);
}
