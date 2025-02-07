package io.github.fabricators_of_create.porting_lib.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.NotNull;

public interface SlopeCreationCheckingRailBlock {
	boolean canMakeSlopes(@NotNull BlockState state, @NotNull BlockView world, @NotNull BlockPos pos);
}
