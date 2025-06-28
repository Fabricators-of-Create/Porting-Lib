package io.github.fabricators_of_create.porting_lib.blocks;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.state.BlockState;

public class ClientBlockHooks {
	public static boolean isBlockInSolidLayer(BlockState state) {
		return ItemBlockRenderTypes.getChunkRenderType(state) != RenderType.solid();
	}
}
