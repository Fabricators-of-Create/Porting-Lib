package io.github.fabricators_of_create.porting_lib.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileWatcher;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.electronwill.nightconfig.toml.TomlFormat;

import net.fabricmc.loader.api.FabricLoader;

import org.apache.commons.io.FilenameUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.function.Function;

public class ModConfig {
	private final ConfigType type;
	private final ModConfigSpec spec;
	private final String fileName;
	private final String modId;
	private CommentedConfig configData;

	public ModConfig(final ConfigType type, final ModConfigSpec spec, final String modId, final String fileName) {
		this.type = type;
		this.spec = spec;
		this.fileName = fileName;
		this.modId = modId;
		ConfigTracker.INSTANCE.trackConfig(this);
	}

	public ModConfig(final ConfigType type, final ModConfigSpec spec, final String modId) {
		this(type, spec, modId, defaultConfigName(type, modId));
	}

	private static String defaultConfigName(ConfigType type, String modId) {
		// config file name would be "forge-client.toml" and "forge-server.toml"
		return String.format(Locale.ROOT, "%s-%s.toml", modId, type.extension());
	}
	public ConfigType getType() {
		return type;
	}

	public String getFileName() {
		return fileName;
	}

	public void unload(Path configBasePath) {
		Path configPath = configBasePath.resolve(getFileName());
		try {
			FileWatcher.defaultInstance().removeWatch(configBasePath.resolve(getFileName()));
		} catch (RuntimeException e) {
			ConfigTracker.LOGGER.error("Failed to remove config {} from tracker!", configPath, e);
		}
	}

	private boolean setupConfigFile(final ModConfig modConfig, final Path file, final ConfigFormat<?> conf) throws IOException {
		Files.createDirectories(file.getParent());
		Path p = FabricLoader.getInstance().getConfigDir().resolve(modConfig.getFileName());
		if (Files.exists(p)) {
			ConfigTracker.LOGGER.info(ConfigTracker.CONFIG, "Loading default config file from path {}", p);
			Files.copy(p, file);
		} else {
			Files.createFile(file);
			conf.initEmptyFile(file);
		}
		return true;
	}

	public Function<ModConfig, CommentedFileConfig> reader(Path configBasePath) {
		return (c) -> {
			final Path configPath = configBasePath.resolve(c.getFileName());
			final CommentedFileConfig configData = CommentedFileConfig.builder(configPath).sync().
					preserveInsertionOrder().
					autosave().
					onFileNotFound((newfile, configFormat)-> setupConfigFile(c, newfile, configFormat)).
					writingMode(WritingMode.REPLACE).
					build();
			ConfigTracker.LOGGER.debug(ConfigTracker.CONFIG, "Built TOML config for {}", configPath.toString());
			try {
				configData.load();
			} catch (ParsingException ex) {
				throw new ConfigLoadingException(c, ex);
			}
			ConfigTracker.LOGGER.debug(ConfigTracker.CONFIG, "Loaded TOML config file {}", configPath.toString());
			try {
				FileWatcher.defaultInstance().addWatch(configPath, new ConfigWatcher(c, configData, Thread.currentThread().getContextClassLoader()));
				ConfigTracker.LOGGER.debug(ConfigTracker.CONFIG, "Watching TOML config file {} for changes", configPath.toString());
			} catch (IOException e) {
				throw new RuntimeException("Couldn't watch config file", e);
			}
			return configData;
		};
	}

	@SuppressWarnings("unchecked")
	public ModConfigSpec getSpec() {
		return spec;
	}

	public String getModId() {
		return modId;
	}

	public CommentedConfig getConfigData() {
		return this.configData;
	}

	void setConfigData(final CommentedConfig configData) {
		this.configData = configData;
		this.spec.setConfig(this.configData);
	}

	public void save() {
		((CommentedFileConfig)this.configData).save();
	}

	public Path getFullPath() {
		return ((CommentedFileConfig)this.configData).getNioPath();
	}

	public void acceptSyncedConfig(byte[] bytes) {
		setConfigData(TomlFormat.instance().createParser().parse(new ByteArrayInputStream(bytes)));
		ConfigEvents.RELOADING.invoker().onModConfigEvent(this);
	}

	public static void backUpConfig(final CommentedFileConfig commentedFileConfig, final int maxBackups) {
		Path bakFileLocation = commentedFileConfig.getNioPath().getParent();
		String bakFileName = FilenameUtils.removeExtension(commentedFileConfig.getFile().getName());
		String bakFileExtension = FilenameUtils.getExtension(commentedFileConfig.getFile().getName()) + ".bak";
		Path bakFile = bakFileLocation.resolve(bakFileName + "-1" + "." + bakFileExtension);
		try {
			for(int i = maxBackups; i > 0; i--) {
				Path oldBak = bakFileLocation.resolve(bakFileName + "-" + i + "." + bakFileExtension);
				if(Files.exists(oldBak)) {
					if(i >= maxBackups)
						Files.delete(oldBak);
					else
						Files.move(oldBak, bakFileLocation.resolve(bakFileName + "-" + (i + 1) + "." + bakFileExtension));
				}
			}
			Files.copy(commentedFileConfig.getNioPath(), bakFile);
		} catch (IOException exception) {
			ConfigTracker.LOGGER.warn(ConfigTracker.CONFIG, "Failed to back up config file {}", commentedFileConfig.getNioPath(), exception);
		}
	}

	private static class ConfigWatcher implements Runnable {
		private final ModConfig modConfig;
		private final CommentedFileConfig commentedFileConfig;
		private final ClassLoader realClassLoader;

		ConfigWatcher(final ModConfig modConfig, final CommentedFileConfig commentedFileConfig, final ClassLoader classLoader) {
			this.modConfig = modConfig;
			this.commentedFileConfig = commentedFileConfig;
			this.realClassLoader = classLoader;
		}

		@Override
		public void run() {
			// Force the regular classloader onto the special thread
			Thread.currentThread().setContextClassLoader(realClassLoader);
			if (!this.modConfig.getSpec().isCorrecting()) {
				try
				{
					this.commentedFileConfig.load();
					if(!this.modConfig.getSpec().isCorrect(commentedFileConfig))
					{
						ConfigTracker.LOGGER.warn(ConfigTracker.CONFIG, "Configuration file {} is not correct. Correcting", commentedFileConfig.getFile().getAbsolutePath());
						backUpConfig(commentedFileConfig, 5);
						this.modConfig.getSpec().correct(commentedFileConfig);
						commentedFileConfig.save();
					}
				}
				catch (ParsingException ex)
				{
					throw new ConfigLoadingException(modConfig, ex);
				}
				ConfigTracker.LOGGER.debug(ConfigTracker.CONFIG, "Config file {} changed, sending notifies", this.modConfig.getFileName());
				this.modConfig.getSpec().afterReload();
				ConfigEvents.RELOADING.invoker().onModConfigEvent(this.modConfig);
			}
		}
	}

	private static class ConfigLoadingException extends RuntimeException
	{
		public ConfigLoadingException(ModConfig config, Exception cause)
		{
			super("Failed loading config file " + config.getFileName() + " of type " + config.getType() + " for modid " + config.getModId(), cause);
		}
	}
}
