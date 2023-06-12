package io.github.fabricators_of_create.porting_lib.config;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class ConfigRegistry {
	protected static final Map<String, EnumMap<ConfigType, ModConfig>> configs = new HashMap<>();

	public static void registerConfig(String modId, ConfigType type, ModConfigSpec spec) {
		if (spec.isEmpty()) {
			// This handles the case where a mod tries to register a config, without any options configured inside it.
			ConfigTracker.LOGGER.debug("Attempted to register an empty config for type {} on mod {}", type, modId);
			return;
		}

		configs.computeIfAbsent(modId, s -> new EnumMap<>(ConfigType.class)).put(type, new ModConfig(type, spec, modId));
	}

	public static void registerConfig(String modId, ConfigType type, ModConfigSpec spec, String fileName) {
		if (spec.isEmpty()) {
			// This handles the case where a mod tries to register a config, without any options configured inside it.
			ConfigTracker.LOGGER.debug("Attempted to register an empty config for type {} on mod {} using file name {}", type, modId, fileName);
			return;
		}

		configs.computeIfAbsent(modId, s -> new EnumMap<>(ConfigType.class)).put(type, new ModConfig(type, spec, modId, fileName));
	}

	public static Map<String, EnumMap<ConfigType, ModConfig>> getConfigs() {
		return configs;
	}
}
