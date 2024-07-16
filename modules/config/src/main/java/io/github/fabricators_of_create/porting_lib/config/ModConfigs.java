package io.github.fabricators_of_create.porting_lib.config;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class provides access to all mod configs known to Porting Lib Config.
 * It can be used by mods that want to process all configs.
 * Configs are registered via {@link ConfigRegistry#registerConfig(String, ModConfig.Type, IConfigSpec)}.
 */
public final class ModConfigs {
	public static List<String> getConfigFileNames(String modId, ModConfig.Type type) {
		var config = ConfigTracker.INSTANCE.configsByMod.getOrDefault(modId, List.of());
		synchronized (config) { // Synchronized list: requires explicit synchronization for stream
			return config.stream()
					.filter(c -> c.getType() == type)
					.map(ModConfig::getFileName)
					.toList();
		}
	}

	public static Set<ModConfig> getConfigSet(ModConfig.Type type) {
		return Collections.unmodifiableSet(ConfigTracker.INSTANCE.configSets.get(type));
	}

	public static Map<String, ModConfig> getFileMap() {
		return Collections.unmodifiableMap(ConfigTracker.INSTANCE.fileMap);
	}
}
