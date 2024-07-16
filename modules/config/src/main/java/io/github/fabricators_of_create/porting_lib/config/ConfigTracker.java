package io.github.fabricators_of_create.porting_lib.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.InMemoryCommentedFormat;
import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig;
import com.electronwill.nightconfig.core.concurrent.ConcurrentCommentedConfig;
import com.electronwill.nightconfig.core.concurrent.SynchronizedConfig;
import com.electronwill.nightconfig.core.file.FileWatcher;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.electronwill.nightconfig.core.io.ParsingMode;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.electronwill.nightconfig.toml.TomlParser;
import com.electronwill.nightconfig.toml.TomlWriter;
import com.mojang.logging.LogUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * The configuration tracker manages various types of mod configurations.
 * Due to the parallel nature of mod initialization, modifying the configuration state must be <strong>thread-safe</strong>.
 */
@ApiStatus.Internal
public class ConfigTracker {
	public static final ConfigTracker INSTANCE = new ConfigTracker();
	static final Marker CONFIG = MarkerFactory.getMarker("CONFIG");
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Path defaultConfigPath = FabricLoader.getInstance().getGameDir().resolve(PortingLibConfig.defaultConfigPath());

	final ConcurrentHashMap<String, ModConfig> fileMap = new ConcurrentHashMap<>();
	final EnumMap<ModConfig.Type, Set<ModConfig>> configSets = new EnumMap<>(ModConfig.Type.class);
	final ConcurrentHashMap<String, List<ModConfig>> configsByMod = new ConcurrentHashMap<>();
	private final Map<String, ReentrantLock> locksByMod = new ConcurrentHashMap<>();
	final ConcurrentHashMap<String, Event<ModConfigEvent.Loading.Callback>> loadingEventsByMod = new ConcurrentHashMap<>();
	final ConcurrentHashMap<String, Event<ModConfigEvent.Reloading.Callback>> reloadingEventsByMod = new ConcurrentHashMap<>();
	final ConcurrentHashMap<String, Event<ModConfigEvent.Unloading.Callback>> unloadingEventsByMod = new ConcurrentHashMap<>();

	@VisibleForTesting
	public ConfigTracker() {
		for (var type : ModConfig.Type.values()) {
			this.configSets.put(type, Collections.synchronizedSet(new LinkedHashSet<>()));
		}
	}

	/**
	 * Registers a new configuration of the given type for a mod, using the default filename for this type of config.
	 * <p>
	 * Registering a configuration is required to receive configuration events.
	 */
	public ModConfig registerConfig(ModConfig.Type type, IConfigSpec spec, ModContainer container) {
		return registerConfig(type, spec, container, defaultConfigName(type, container.getMetadata().getId()));
	}

	/**
	 * Registers a new configuration of the given type for a mod, using a custom filename.
	 * <p>
	 * Registering a configuration is required to receive configuration events.
	 */
	public ModConfig registerConfig(ModConfig.Type type, IConfigSpec spec, ModContainer container, String fileName) {
		var lock = locksByMod.computeIfAbsent(container.getMetadata().getId(), m -> new ReentrantLock());
		var modConfig = new ModConfig(type, spec, container, fileName, lock);
		trackConfig(modConfig);

		if (modConfig.getType() == ModConfig.Type.STARTUP) {
			openConfig(modConfig, FabricLoader.getInstance().getConfigDir(), null);
		}

		return modConfig;
	}

	private static String defaultConfigName(ModConfig.Type type, String modId) {
		// for mod-id "forge", config file name would be "forge-client.toml" and "forge-server.toml"
		return String.format(Locale.ROOT, "%s-%s.toml", modId, type.extension());
	}

	void trackConfig(ModConfig config) {
		var previousValue = this.fileMap.putIfAbsent(config.getFileName(), config);
		if (previousValue != null) {
			LOGGER.error(CONFIG, "Detected config file conflict {} between {} and {}", config.getFileName(), previousValue.getModId(), config.getModId());
			throw new RuntimeException("Config conflict detected!");
		}
		this.configSets.get(config.getType()).add(config);
		this.configsByMod.computeIfAbsent(config.getModId(), (k) -> Collections.synchronizedList(new ArrayList<>())).add(config);
		LOGGER.debug(CONFIG, "Config file {} for {} tracking", config.getFileName(), config.getModId());
	}

	public void loadConfigs(ModConfig.Type type, Path configBasePath) {
		loadConfigs(type, configBasePath, null);
	}

	public void loadConfigs(ModConfig.Type type, Path configBasePath, @Nullable Path configOverrideBasePath) {
		LOGGER.debug(CONFIG, "Loading configs type {}", type);
		this.configSets.get(type).forEach(config -> openConfig(config, configBasePath, configOverrideBasePath));
	}

	public void unloadConfigs(ModConfig.Type type) {
		LOGGER.debug(CONFIG, "Unloading configs type {}", type);
		this.configSets.get(type).forEach(ConfigTracker::closeConfig);
	}

	static void openConfig(ModConfig config, Path configBasePath, @Nullable Path configOverrideBasePath) {
		LOGGER.trace(CONFIG, "Loading config file type {} at {} for {}", config.getType(), config.getFileName(), config.getModId());
		if (config.loadedConfig != null) {
			LOGGER.warn("Opening a config that was already loaded with value {} at path {}", config.loadedConfig, config.getFileName());
		}
		var basePath = resolveBasePath(config, configBasePath, configOverrideBasePath);
		var configPath = basePath.resolve(config.getFileName());

		loadConfig(config, configPath, ModConfigEvent.Loading::new);
		LOGGER.debug(CONFIG, "Loaded TOML config file {}", configPath);

		if (!PortingLibConfig.getBoolConfigValue(PortingLibConfig.ConfigValue.DISABLE_CONFIG_WATCHER)) {
			FileWatcher.defaultInstance().addWatch(configPath, new ConfigWatcher(config, configPath, Thread.currentThread().getContextClassLoader()));
			LOGGER.debug(CONFIG, "Watching TOML config file {} for changes", configPath);
		}
	}

	private static Path resolveBasePath(ModConfig config, Path configBasePath, @Nullable Path configOverrideBasePath) {
		if (configOverrideBasePath != null) {
			Path overrideFilePath = configOverrideBasePath.resolve(config.getFileName());
			if (Files.exists(overrideFilePath)) {
				LOGGER.info(CONFIG, "Found config file override in path {}", overrideFilePath);
				return configOverrideBasePath;
			}
		}
		return configBasePath;
	}

	static void loadConfig(ModConfig modConfig, Path path, Function<ModConfig, ModConfigEvent> eventConstructor) {
		CommentedConfig config;

		try {
			// Load existing config
			config = readConfig(path);

			if (!modConfig.getSpec().isCorrect(config)) {
				LOGGER.warn(CONFIG, "Configuration file {} is not correct. Correcting", path);
				backUpConfig(path);
				modConfig.getSpec().correct(config);
				writeConfig(path, config);
			}
		} catch (NoSuchFileException ignored) {
			// Config file does not exist yet
			try {
				setupConfigFile(modConfig, path);
				config = readConfig(path);
			} catch (IOException | ParsingException ex) {
				throw new RuntimeException("Failed to create default config file " + modConfig.getFileName() + " of type " + modConfig.getType() + " for modid " + modConfig.getModId(), ex);
			}
		} catch (IOException | ParsingException ex) {
			// Failed to read existing file
			LOGGER.warn(CONFIG, "Failed to load config {}: {}. Attempting to recreate", modConfig.getFileName(), ex);
			try {
				backUpConfig(path);
				Files.delete(path);

				setupConfigFile(modConfig, path);
				config = readConfig(path);
			} catch (Throwable t) {
				ex.addSuppressed(t);

				throw new RuntimeException("Failed to recreate config file " + modConfig.getFileName() + " of type " + modConfig.getType() + " for modid " + modConfig.getModId(), ex);
			}
		}

		modConfig.setConfig(new LoadedConfig(config, path, modConfig), eventConstructor);
	}

	public static void acceptSyncedConfig(ModConfig modConfig, byte[] bytes) {
		if (modConfig.loadedConfig != null) {
			LOGGER.warn("Overwriting non-null config {} at path {} with synced config", modConfig.loadedConfig, modConfig.getFileName());
		}
		var newConfig = new SynchronizedConfig(InMemoryCommentedFormat.defaultInstance(), LinkedHashMap::new);
		newConfig.bulkCommentedUpdate(view -> {
			TomlFormat.instance().createParser().parse(new ByteArrayInputStream(bytes), view, ParsingMode.REPLACE);
		});
		// TODO: do we want to do any validation? (what do we do if acceptConfig fails?)
		modConfig.setConfig(new LoadedConfig(newConfig, null, modConfig), ModConfigEvent.Reloading::new); // TODO: should maybe be Loading on the first load?
	}

	public void loadDefaultServerConfigs() {
		configSets.get(ModConfig.Type.SERVER).forEach(modConfig -> {
			if (modConfig.loadedConfig != null) {
				LOGGER.warn("Overwriting non-null config {} at path {} with default server config", modConfig.loadedConfig, modConfig.getFileName());
			}

			modConfig.setConfig(new LoadedConfig(createDefaultConfig(modConfig.getSpec()), null, modConfig), ModConfigEvent.Loading::new);
		});
	}

	private static CommentedConfig createDefaultConfig(IConfigSpec spec) {
		var commentedConfig = new SynchronizedConfig(InMemoryCommentedFormat.defaultInstance(), LinkedHashMap::new);
		commentedConfig.bulkCommentedUpdate(spec::correct);
		return commentedConfig;
	}

	private static void closeConfig(ModConfig config) {
		if (config.loadedConfig != null) {
			if (config.loadedConfig.path() != null) {
				LOGGER.trace(CONFIG, "Closing config file type {} at {} for {}", config.getType(), config.getFileName(), config.getModId());
				unload(config.loadedConfig.path());
				config.setConfig(null, ModConfigEvent.Unloading::new);
			} else {
				LOGGER.warn(CONFIG, "Closing non-file config {} at path {}", config.loadedConfig, config.getFileName());
			}
		}
	}

	private static void unload(Path path) {
		if (PortingLibConfig.getBoolConfigValue(PortingLibConfig.ConfigValue.DISABLE_CONFIG_WATCHER))
			return;
		try {
			FileWatcher.defaultInstance().removeWatch(path);
		} catch (RuntimeException e) {
			LOGGER.error("Failed to remove config {} from tracker!", path, e);
		}
	}

	private static void setupConfigFile(ModConfig modConfig, Path file) throws IOException {
		Files.createDirectories(file.getParent());
		Path p = defaultConfigPath.resolve(modConfig.getFileName());
		if (Files.exists(p)) {
			LOGGER.info(CONFIG, "Loading default config file from path {}", p);
			Files.copy(p, file);
		} else {
			writeConfig(file, createDefaultConfig(modConfig.getSpec()));
		}
	}

	private static ConcurrentCommentedConfig readConfig(Path path) throws IOException, ParsingException {
		try (var reader = Files.newBufferedReader(path)) {
			var config = new SynchronizedConfig(TomlFormat.instance(), LinkedHashMap::new);
			config.bulkCommentedUpdate(view -> {
				new TomlParser().parse(reader, view, ParsingMode.REPLACE);
			});
			return config;
		}
	}

	static void writeConfig(Path file, UnmodifiableCommentedConfig config) {
		new TomlWriter().write(config, file, WritingMode.REPLACE_ATOMIC);
	}

	private static void backUpConfig(Path commentedFileConfig) {
		backUpConfig(commentedFileConfig, 5); //TODO: Think of a way for mods to set their own preference (include a sanity check as well, no disk stuffing)
	}

	private static void backUpConfig(Path commentedFileConfig, int maxBackups) {
		Path bakFileLocation = commentedFileConfig.getParent();
		String bakFileName = FilenameUtils.removeExtension(commentedFileConfig.getFileName().toString());
		String bakFileExtension = FilenameUtils.getExtension(commentedFileConfig.getFileName().toString()) + ".bak";
		Path bakFile = bakFileLocation.resolve(bakFileName + "-1" + "." + bakFileExtension);
		try {
			for (int i = maxBackups; i > 0; i--) {
				Path oldBak = bakFileLocation.resolve(bakFileName + "-" + i + "." + bakFileExtension);
				if (Files.exists(oldBak)) {
					if (i >= maxBackups)
						Files.delete(oldBak);
					else
						Files.move(oldBak, bakFileLocation.resolve(bakFileName + "-" + (i + 1) + "." + bakFileExtension));
				}
			}
			Files.copy(commentedFileConfig, bakFile);
		} catch (IOException exception) {
			LOGGER.warn(CONFIG, "Failed to back up config file {}", commentedFileConfig, exception);
		}
	}
}
