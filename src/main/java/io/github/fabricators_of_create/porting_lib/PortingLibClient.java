package io.github.fabricators_of_create.porting_lib;

import io.github.fabricators_of_create.porting_lib.entity.ExtraSpawnDataEntity;
import io.github.fabricators_of_create.porting_lib.event.MinecraftTailCallback;
import io.github.fabricators_of_create.porting_lib.model.ModelLoaderRegistry;
import io.github.fabricators_of_create.porting_lib.util.FluidTextUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;

public class PortingLibClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(FluidTextUtil.NUMBER_FORMAT);
		MinecraftTailCallback.EVENT.register(mc -> ModelLoaderRegistry.init());
		ExtraSpawnDataEntity.initClientNetworking();
	}
}
