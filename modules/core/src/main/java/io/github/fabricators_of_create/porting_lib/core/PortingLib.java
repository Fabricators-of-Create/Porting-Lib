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
	public static final String NAME = "Porting Lib";
	public static final Logger LOGGER = LoggerFactory.getLogger(NAME);
	// enables some extra safety checks.
	public static final boolean STRICT_VALIDATION = FabricLoader.getInstance().isDevelopmentEnvironment()
			|| Boolean.getBoolean("portingLib.strictValidation");

	public static ResourceLocation id(String path) {
		return new ResourceLocation(ID, path);
	}
}
