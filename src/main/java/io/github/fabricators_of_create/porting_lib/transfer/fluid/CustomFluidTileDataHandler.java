package io.github.fabricators_of_create.porting_lib.transfer.fluid;

import io.github.fabricators_of_create.porting_lib.util.FluidTileDataHandler.FluidTankData;
import net.minecraft.core.BlockPos;

import java.util.Map;

public interface CustomFluidTileDataHandler {
	FluidTankData[] getData(Map<BlockPos, FluidTankData[]> cached);
}
