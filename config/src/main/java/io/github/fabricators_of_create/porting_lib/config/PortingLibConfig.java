package io.github.fabricators_of_create.porting_lib.config;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerLoginConnectionEvents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

public class PortingLibConfig implements ModInitializer {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final LevelResource SERVERCONFIG = new LevelResource("serverconfig");

	private Path getServerConfigPath(final MinecraftServer server) {
		final Path serverConfig = server.getWorldPath(SERVERCONFIG);
		getOrCreateDirectory(serverConfig, "serverconfig");
		return serverConfig;
	}

	public static final ResourceLocation CONFIG_SYNC = new ResourceLocation("porting_lib_config", "config_sync");

	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
			ConfigTracker.INSTANCE.loadConfigs(ConfigType.SERVER, getServerConfigPath(server));
		});
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
			ConfigTracker.INSTANCE.unloadConfigs(ConfigType.SERVER, getServerConfigPath(server));
		});

		ServerLoginConnectionEvents.QUERY_START.register((handler, server, sender, synchronizer) -> {
			final Map<String, byte[]> configData = ConfigTracker.INSTANCE.configSets().get(ConfigType.SERVER).stream().collect(Collectors.toMap(ModConfig::getFileName, mc -> {
				try {
					return Files.readAllBytes(mc.getFullPath());
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}));
			configData.forEach((key, value) -> {
				FriendlyByteBuf buf = PacketByteBufs.create();
				buf.writeUtf(key);
				buf.writeByteArray(value);
				sender.sendPacket(CONFIG_SYNC, buf);
			});
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
