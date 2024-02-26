package io.github.fabricators_of_create.porting_lib.extensions;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.ApiStatus;

// Use to avoid quarky class loading
@ApiStatus.Internal
public class ClientExtensionHooks {
	public static boolean isBlockInSolidLayer(BlockState state) {
		return ItemBlockRenderTypes.getChunkRenderType(state) != RenderType.solid();
	}
}
