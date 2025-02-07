package io.github.fabricators_of_create.porting_lib.client;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import org.jetbrains.annotations.ApiStatus;

// Use to avoid quarky class loading
@ApiStatus.Internal
public class ClientExtensionHooks {
	public static boolean isBlockInSolidLayer(BlockState state) {
		return RenderLayers.getBlockLayer(state) != RenderLayer.getSolid();
	}
}
