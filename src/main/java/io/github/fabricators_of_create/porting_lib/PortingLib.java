package io.github.fabricators_of_create.porting_lib;

import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.ResourceLocation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PortingLib implements ModInitializer {
	public static final String ID = "porting_lib";
	public static final Logger LOGGER = LoggerFactory.getLogger(ID);

	@Override
	public void onInitialize() {
	}

	public static ResourceLocation id(String path) {
		return new ResourceLocation(ID, path);
	}
}
