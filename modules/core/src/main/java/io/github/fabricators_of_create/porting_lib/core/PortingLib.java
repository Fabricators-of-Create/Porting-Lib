package io.github.fabricators_of_create.porting_lib.core;

import net.fabricmc.loader.api.FabricLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.resources.ResourceLocation;

/**
 * Core constants and utils.
 */
public class PortingLib {
	public static final String ID = "porting_lib";
	public static final String NEO_ID = "neoforge";
	public static final String NAME = "Porting Lib";
	public static final Logger LOGGER = LoggerFactory.getLogger(NAME);
	public static final boolean DEBUG = FabricLoader.getInstance().isDevelopmentEnvironment()
			|| Boolean.getBoolean("portingLib.debug");

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(ID, path);
	}

	public static ResourceLocation neo(String path) {
		return ResourceLocation.fromNamespaceAndPath(NEO_ID, path);
	}

	public static RuntimeException createMixinException(String extension) {
		return new UnsupportedOperationException("Implementation does not support extension: " + extension);
	}
}
