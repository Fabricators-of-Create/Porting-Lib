package io.github.fabricators_of_create.porting_lib.config;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.ConfigSpec;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;

import com.electronwill.nightconfig.core.file.FileNotFoundAction;

import com.electronwill.nightconfig.core.io.ParsingException;
import com.electronwill.nightconfig.core.io.WritingMode;

import io.github.fabricators_of_create.porting_lib.config.network.configuration.SyncConfig;
import io.github.fabricators_of_create.porting_lib.config.network.payload.ConfigFilePayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;

import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;

import net.fabricmc.loader.api.FabricLoader;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

public class PortingLibConfig {
	public static final String ID = "porting_lib_config";
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final PortingLibConfig INSTANCE = new PortingLibConfig();
	public static final Path PORTING_LIB_CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("porting_lib_config.toml");
	private static final ConfigSpec configSpec = new ConfigSpec();
	private static final CommentedConfig configComments = CommentedConfig.inMemory();
	static {
		for (ConfigValue cv : ConfigValue.values()) {
			cv.buildConfigEntry(configSpec, configComments);
		}
		load();
	}

	private CommentedFileConfig configData;
	private static final LevelResource SERVERCONFIG = new LevelResource("serverconfig");

	public enum ConfigValue {
		DISABLE_CONFIG_WATCHER("disableConfigWatcher", Boolean.FALSE, "Disables File Watcher. Used to automatically update config if its file has been modified."),
		DEFAULT_CONFIG_PATH("defaultConfigPath", "defaultconfigs", "Default config path for servers");

		private final String entry;
		private final Object defaultValue;
		private final String comment;
		private final Class<?> valueType;
		private final Function<Object, Object> entryFunction;

		ConfigValue(final String entry, final Object defaultValue, final String comment) {
			this(entry, defaultValue, comment, Function.identity());
		}

		ConfigValue(final String entry, final Object defaultValue, final String comment, Function<Object, Object> entryFunction) {
			this.entry = entry;
			this.defaultValue = defaultValue;
			this.comment = comment;
			this.valueType = defaultValue.getClass();
			this.entryFunction = entryFunction;
		}

		void buildConfigEntry(ConfigSpec spec, CommentedConfig commentedConfig) {
			if (this.defaultValue instanceof List<?> list) {
				spec.defineList(this.entry, list, e -> e instanceof String);
			} else {
				spec.define(this.entry, this.defaultValue);
			}
			commentedConfig.add(this.entry, this.defaultValue);
			commentedConfig.setComment(this.entry, this.comment);
		}

		@SuppressWarnings("unchecked")
		private <T> T getConfigValue(CommentedFileConfig config) {
			return (T) this.entryFunction.apply(config != null ? config.get(this.entry) : this.defaultValue);
		}

		public <T> void updateValue(final CommentedFileConfig configData, final T value) {
			configData.set(this.entry, value);
		}
	}

	private void loadFrom(final Path configFile) {
		configData = CommentedFileConfig.builder(configFile).sync()
				.onFileNotFound(FileNotFoundAction.copyData(Objects.requireNonNull(getClass().getResourceAsStream("/META-INF/defaultportinglibconfig.toml"))))
				.writingMode(WritingMode.REPLACE)
				.build();
		try {
			configData.load();
		} catch (ParsingException e) {
			throw new RuntimeException("Failed to load Porting Lib config from " + configFile, e);
		}
		if (!configSpec.isCorrect(configData)) {
			LOGGER.warn("Configuration file {} is not correct. Correcting", configFile);
			configSpec.correct(configData, (action, path, incorrectValue, correctedValue) -> LOGGER.info("Incorrect key {} was corrected from {} to {}", path, incorrectValue, correctedValue));
		}
		configData.putAllComments(configComments);
		configData.save();
	}

	public static void load() {
		final Path configFile = PORTING_LIB_CONFIG_FILE;
		INSTANCE.loadFrom(configFile);
		if (LOGGER.isTraceEnabled()) {
			LOGGER.trace("Loaded Porting Lib config from {}", configFile);
			for (ConfigValue cv : ConfigValue.values()) {
				LOGGER.trace("PortingLibConfig {} is {}", cv.entry, cv.getConfigValue(INSTANCE.configData));
			}
		}
		getOrCreateGameRelativePath(Paths.get(getConfigValue(ConfigValue.DEFAULT_CONFIG_PATH)));
	}

	public static Path getOrCreateGameRelativePath(Path path) {
		Path gameFolderPath = FabricLoader.getInstance().getGameDir().resolve(path);

		if (!Files.isDirectory(gameFolderPath)) {
			try {
				Files.createDirectories(gameFolderPath);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		return gameFolderPath;
	}

	public static String getConfigValue(ConfigValue v) {
		return v.getConfigValue(INSTANCE.configData);
	}

	public static boolean getBoolConfigValue(ConfigValue v) {
		return v.getConfigValue(INSTANCE.configData);
	}

	private static Path getServerConfigPath(MinecraftServer server) {
		Path serverConfig = server.getWorldPath(SERVERCONFIG);
		getOrCreateDirectory(serverConfig, "serverconfig");
		return serverConfig;
	}

	public static void init() {
		PayloadTypeRegistry.configurationS2C().register(ConfigFilePayload.TYPE, ConfigFilePayload.STREAM_CODEC);
		ServerConfigurationConnectionEvents.CONFIGURE.register((handler, server) -> {
			if (ServerConfigurationNetworking.canSend(handler, ConfigFilePayload.TYPE)) {
				handler.addTask(new SyncConfig(handler));
			}
		});
		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
			ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.SERVER, getServerConfigPath(server));
		});
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
			ConfigTracker.INSTANCE.unloadConfigs(ModConfig.Type.SERVER);
		});
	}

	private static Path getOrCreateDirectory(Path dirPath, String dirLabel) {
		if (!Files.isDirectory(dirPath.getParent())) {
			getOrCreateDirectory(dirPath.getParent(), "parent of "+dirLabel);
		}
		if (!Files.isDirectory(dirPath)) {
			LOGGER.debug("Making {} directory : {}", dirLabel, dirPath);
			try {
				Files.createDirectory(dirPath);
			} catch (IOException e) {
				if (e instanceof FileAlreadyExistsException) {
					LOGGER.error("Failed to create {} directory - there is a file in the way", dirLabel);
				} else {
					LOGGER.error("Problem with creating {} directory (Permissions?)", dirLabel, e);
				}
				throw new RuntimeException("Problem creating directory", e);
			}
			LOGGER.debug("Created {} directory : {}", dirLabel, dirPath);
		} else {
			LOGGER.debug("Found existing {} directory : {}", dirLabel, dirPath);
		}
		return dirPath;
	}

	public static <T> void updateConfig(ConfigValue v, T value) {
		if (INSTANCE.configData != null) {
			v.updateValue(INSTANCE.configData, value);
			INSTANCE.configData.save();
		}
	}

	public static String defaultConfigPath() {
		return getConfigValue(ConfigValue.DEFAULT_CONFIG_PATH);
	}
}
