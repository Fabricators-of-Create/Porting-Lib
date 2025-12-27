package io.github.fabricators_of_create.porting_lib.blocks.client;

import io.github.fabricators_of_create.porting_lib.blocks.SidedHelper;
import net.minecraft.world.level.block.state.BlockState;

public class ClientSidedHelper extends SidedHelper {
	@Override
	public boolean isBlockInSolidLayer(BlockState state) {
		return ClientBlockHooks.isBlockInSolidLayer(state);
	}
}
