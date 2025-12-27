package io.github.fabricators_of_create.porting_lib.blocks;

import net.minecraft.world.level.block.state.BlockState;

public class SidedHelper {
	public static SidedHelper INSTANCE = new SidedHelper();

	public boolean isBlockInSolidLayer(BlockState state) {
		return true;
	}
}
