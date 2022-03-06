package io.github.fabricators_of_create.porting_lib;

import io.github.fabricators_of_create.porting_lib.event.MinecraftTailCallback;
import io.github.fabricators_of_create.porting_lib.model.ModelLoaderRegistry;
import io.github.fabricators_of_create.porting_lib.util.FluidHandlerData;
import io.github.fabricators_of_create.porting_lib.util.FluidTextUtil;
import io.github.fabricators_of_create.porting_lib.util.FluidTileDataHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;

public class PortingLibClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(FluidTextUtil.NUMBER_FORMAT);
		FluidHandlerData.initClient();
		FluidTileDataHandler.initClient();
		MinecraftTailCallback.EVENT.register(mc -> ModelLoaderRegistry.init());
	}
}
