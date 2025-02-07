package io.github.fabricators_of_create.porting_lib.extensions;

import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.RailShape;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public interface BaseRailBlockExtensions {
	default RailShape getRailDirection(BlockState state, BlockView world, BlockPos pos, @Nullable AbstractRailBlock block) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
