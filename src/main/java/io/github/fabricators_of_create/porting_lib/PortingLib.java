package io.github.fabricators_of_create.porting_lib;

import io.github.fabricators_of_create.porting_lib.util.FluidTextUtil;
import io.github.fabricators_of_create.porting_lib.util.ServerLifecycleHooks;
import io.github.fabricators_of_create.porting_lib.util.TierSortingRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PortingLib implements ModInitializer, ClientModInitializer {
	public static final String ID = "porting_lib";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	@Override
	public void onInitialize() {
		TierSortingRegistry.init();
		ServerLifecycleHooks.init();
	}

	@Override
	public void onInitializeClient() {
		ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
		if (resourceManager instanceof ReloadableResourceManager reloadable)
			reloadable.registerReloadListener(FluidTextUtil.NUMBER_FORMAT);
	}

	public static ResourceLocation id(String path) {
		return new ResourceLocation(ID, path);
	}
}
