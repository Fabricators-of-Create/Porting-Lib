package io.github.fabricators_of_create.porting_lib.config;

import java.nio.file.Path;
import java.util.Locale;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

import net.fabricmc.loader.api.ModContainer;

import org.jetbrains.annotations.Nullable;

public final class ModConfig {
	private final Type type;
	private final IConfigSpec spec;
	private final String fileName;
	final ModContainer container;
	@Nullable
	LoadedConfig loadedConfig;
	/**
	 * NightConfig's own configs are threadsafe, but mod code is not necessarily.
	 * This lock is used to prevent multiple concurrent config reloads or event dispatches.
	 */
	final Lock lock;

	ModConfig(Type type, IConfigSpec spec, ModContainer container, String fileName, ReentrantLock lock) {
		this.type = type;
		this.spec = spec;
		this.fileName = fileName;
		this.container = container;
		this.lock = lock;
	}

	public Type getType() {
		return type;
	}

	public String getFileName() {
		return fileName;
	}

	public IConfigSpec getSpec() {
		return spec;
	}

	public String getModId() {
		return container.getMetadata().getId();
	}

	// TODO: remove from public API?
	public Path getFullPath() {
		if (this.loadedConfig != null && loadedConfig.path() != null) {
			return loadedConfig.path();
		} else {
			throw new IllegalStateException("Cannot call getFullPath() on non-file config " + this.loadedConfig + " at path " + getFileName());
		}
	}

	void setConfig(@Nullable LoadedConfig loadedConfig, Function<ModConfig, ModConfigEvent> eventConstructor) {
		lock.lock();

		try {
			this.loadedConfig = loadedConfig;
			spec.acceptConfig(loadedConfig);
			eventConstructor.apply(this).sendEvent();
		} finally {
			lock.unlock();
		}
	}

	public enum Type {
		/**
		 * Common mod config for configuration that needs to be loaded on both environments.
		 * Loaded on both servers and clients.
		 * Stored in the global config directory.
		 * Not synced.
		 * Suffix is "-common" by default.
		 */
		COMMON,
		/**
		 * Client config is for configuration affecting the ONLY client state such as graphical options.
		 * Only loaded on the client side.
		 * Stored in the global config directory.
		 * Not synced.
		 * Suffix is "-client" by default.
		 */
		CLIENT,
		/**
		 * Server type config is configuration that is associated with a server instance.
		 * Only loaded during server startup.
		 * Stored in a server/save specific "serverconfig" directory.
		 * Synced to clients during connection.
		 * Suffix is "-server" by default.
		 */
		SERVER,
		/**
		 * Startup configs are for configurations that need to run as early as possible.
		 * Loaded as soon as the config is registered to PLC.
		 * Please be aware when using them, as using these configs to enable/disable registration and anything that must be present on both sides
		 * can cause clients to have issues connecting to servers with different config values.
		 * Stored in the global config directory.
		 * Not synced.
		 * Suffix is "-startup" by default.
		 */
		STARTUP;

		public String extension() {
			return name().toLowerCase(Locale.ROOT);
		}
	}
}
