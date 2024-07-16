package io.github.fabricators_of_create.porting_lib;

import io.github.fabricators_of_create.porting_lib.event.common.ModsLoadedCallback;

import io.github.fabricators_of_create.porting_lib.util.DeferredSpawnEggItem;
import net.fabricmc.api.EnvType;

import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.util.FastColor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.fabricators_of_create.porting_lib.util.FluidTextUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;

public class PortingLibBaseClient implements ClientModInitializer {
	private final Logger LOGGER = LoggerFactory.getLogger("porting_lib_client");

	@Override
	public void onInitializeClient() {
		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(FluidTextUtil.NUMBER_FORMAT);

		ModsLoadedCallback.EVENT.register(envType -> {
			if (envType == EnvType.CLIENT) {
				DeferredSpawnEggItem.MOD_EGGS.forEach(egg -> ColorProviderRegistry.ITEM.register((stack, layer) -> FastColor.ARGB32.opaque(egg.getColor(layer)), egg));
			}
		});
	}
}
