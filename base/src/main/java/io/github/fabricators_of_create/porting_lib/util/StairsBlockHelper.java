package io.github.fabricators_of_create.porting_lib.util;

import io.github.fabricators_of_create.porting_lib.mixin.accessors.common.accessor.StairBlockAccessor;

import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;

public class StairsBlockHelper {
	public static StairBlock init(BlockState blockState, Properties properties) {
		return StairBlockAccessor.port_lib$init(blockState, properties);
	}
}
