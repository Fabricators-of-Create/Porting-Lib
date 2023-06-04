package io.github.fabricators_of_create.porting_lib.config;

import com.mojang.logging.LogUtils;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PortingLibConfig implements ModInitializer {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final LevelResource SERVERCONFIG = new LevelResource("serverconfig");

	private Path getServerConfigPath(final MinecraftServer server) {
		final Path serverConfig = server.getWorldPath(SERVERCONFIG);
		getOrCreateDirectory(serverConfig, "serverconfig");
		return serverConfig;
	}

	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
			ConfigTracker.INSTANCE.loadConfigs(ConfigType.SERVER, getServerConfigPath(server));
		});
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
			ConfigTracker.INSTANCE.unloadConfigs(ConfigType.SERVER, getServerConfigPath(server));
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
}
