package io.github.fabricators_of_create.porting_lib.block;

import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface SlopeCreationCheckingRailBlock {
	boolean canMakeSlopes(@Nonnull BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos);
}
