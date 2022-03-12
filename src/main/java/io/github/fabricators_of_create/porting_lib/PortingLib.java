package io.github.fabricators_of_create.porting_lib;

import io.github.fabricators_of_create.porting_lib.attributes.PortingLibAttributes;
import io.github.fabricators_of_create.porting_lib.transfer.item.item.ItemItemStorages;
import io.github.fabricators_of_create.porting_lib.util.ServerLifecycleHooks;
import io.github.fabricators_of_create.porting_lib.util.TierSortingRegistry;
import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.ResourceLocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PortingLib implements ModInitializer {
	public static final String ID = "porting_lib";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	@Override
	public void onInitialize() {
		TierSortingRegistry.init();
		ServerLifecycleHooks.init();
		PortingLibAttributes.init();
		new ItemItemStorages();
	}

	public static ResourceLocation id(String path) {
		return new ResourceLocation(ID, path);
	}
}
