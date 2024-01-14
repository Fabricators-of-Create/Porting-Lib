package io.github.fabricators_of_create.porting_lib.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.mojang.logging.LogUtils;

import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.nio.file.Path;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigTracker {
	public static final Logger LOGGER = LogUtils.getLogger();
	static final Marker CONFIG = MarkerFactory.getMarker("CONFIG");
	public static final ConfigTracker INSTANCE = new ConfigTracker();
	private final ConcurrentHashMap<String, ModConfig> fileMap;
	private final EnumMap<ConfigType, Set<ModConfig>> configSets;
	private final ConcurrentHashMap<String, Map<ConfigType, ModConfig>> configsByMod;

	private ConfigTracker() {
		this.fileMap = new ConcurrentHashMap<>();
		this.configSets = new EnumMap<>(ConfigType.class);
		this.configsByMod = new ConcurrentHashMap<>();
		this.configSets.put(ConfigType.CLIENT, Collections.synchronizedSet(new LinkedHashSet<>()));
		this.configSets.put(ConfigType.COMMON, Collections.synchronizedSet(new LinkedHashSet<>()));
		this.configSets.put(ConfigType.SERVER, Collections.synchronizedSet(new LinkedHashSet<>()));
	}

	void trackConfig(final ModConfig config) {
		if (this.fileMap.containsKey(config.getFileName())) {
			LOGGER.error(CONFIG,"Detected config file conflict {} between {} and {}", config.getFileName(), this.fileMap.get(config.getFileName()).getModId(), config.getModId());
			throw new RuntimeException("Config conflict detected!");
		}
		this.fileMap.put(config.getFileName(), config);
		this.configSets.get(config.getType()).add(config);
		this.configsByMod.computeIfAbsent(config.getModId(), (k)->new EnumMap<>(ConfigType.class)).put(config.getType(), config);
		LOGGER.debug(CONFIG, "Config file {} for {} tracking", config.getFileName(), config.getModId());
	}

	public void loadConfigs(ConfigType type, Path configBasePath) {
		LOGGER.debug(CONFIG, "Loading configs type {}", type);
		this.configSets.get(type).forEach(config -> openConfig(config, configBasePath));
	}

	public void unloadConfigs(ConfigType type, Path configBasePath) {
		LOGGER.debug(CONFIG, "Unloading configs type {}", type);
		this.configSets.get(type).forEach(config -> closeConfig(config, configBasePath));
	}

	private void openConfig(final ModConfig config, final Path configBasePath) {
		LOGGER.trace(CONFIG, "Loading config file type {} at {} for {}", config.getType(), config.getFileName(), config.getModId());
		final CommentedFileConfig configData = config.reader(configBasePath).apply(config);
		config.setConfigData(configData);
		ConfigEvents.LOADING.invoker().onModConfigEvent(config);
		config.save();
	}

	private void closeConfig(final ModConfig config, final Path configBasePath) {
		if (config.getConfigData() != null) {
			LOGGER.trace(CONFIG, "Closing config file type {} at {} for {}", config.getType(), config.getFileName(), config.getModId());
			// stop the filewatcher before we save the file and close it, so reload doesn't fire
			config.unload(configBasePath);
			ConfigEvents.UNLOADING.invoker().onModConfigEvent(config);
			config.save();
			config.setConfigData(null);
		}
	}

	public void loadDefaultServerConfigs() {
		configSets.get(ConfigType.SERVER).forEach(modConfig -> {
			final CommentedConfig commentedConfig = CommentedConfig.inMemory();
			modConfig.getSpec().correct(commentedConfig);
			modConfig.setConfigData(commentedConfig);
			ConfigEvents.LOADING.invoker().onModConfigEvent(modConfig);
		});
	}

	public String getConfigFileName(String modId, ConfigType type) {
		return Optional.ofNullable(configsByMod.getOrDefault(modId, Collections.emptyMap()).getOrDefault(type, null)).
				map(ModConfig::getFullPath).map(Object::toString).orElse(null);
	}

	public Map<ConfigType, Set<ModConfig>> configSets() {
		return configSets;
	}

	public ConcurrentHashMap<String, ModConfig> fileMap() {
		return fileMap;
	}
}
