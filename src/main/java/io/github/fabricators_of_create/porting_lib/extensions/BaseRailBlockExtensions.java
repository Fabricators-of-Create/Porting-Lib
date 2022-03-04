package io.github.fabricators_of_create.porting_lib.extensions;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;

public interface BaseRailBlockExtensions {
	default RailShape getRailDirection(BlockState state, BlockGetter world, BlockPos pos, @Nullable BaseRailBlock block) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
