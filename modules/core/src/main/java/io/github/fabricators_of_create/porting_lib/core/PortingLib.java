package io.github.fabricators_of_create.porting_lib.core;

import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(ID, path);
	}

	public static <T> ResourceKey<Registry<T>> key(String path) {
		return ResourceKey.createRegistryKey(id(path));
	}

	public static Identifier neo(String path) {
		return Identifier.fromNamespaceAndPath(NEO_ID, path);
	}

	public static RuntimeException createMixinException(String extension) {
		return new UnsupportedOperationException("Implementation does not support extension: " + extension);
	}
}
