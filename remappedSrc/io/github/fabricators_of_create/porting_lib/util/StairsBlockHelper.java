package io.github.fabricators_of_create.porting_lib.util;

import io.github.fabricators_of_create.porting_lib.mixin.common.accessor.StairBlockAccessor;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;

public class StairsBlockHelper {
	public static StairsBlock init(BlockState blockState, Settings properties) {
		return StairBlockAccessor.port_lib$init(blockState, properties);
	}
}
