package io.github.fabricators_of_create.porting_lib.config;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import net.minecraft.server.level.ServerPlayer;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;

public class PortingLibConfig implements ModInitializer {
	public static final String ID = "porting_lib_config";
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final LevelResource SERVERCONFIG = new LevelResource("serverconfig");

	private Path getServerConfigPath(MinecraftServer server) {
		Path serverConfig = server.getWorldPath(SERVERCONFIG);
		getOrCreateDirectory(serverConfig, "serverconfig");
		return serverConfig;
	}

	/**
	 * The ID for the config sync packet.
	 */
	public static final ResourceLocation CONFIG_SYNC = new ResourceLocation(ID, "config_sync");
	/**
	 * An event phase for {@link ServerPlayConnectionEvents#JOIN} that comes after config sync.
	 * If you need to send packets to players on join that depend on config values already being synced,
	 * register using this phase.
	 */
	public static final ResourceLocation AFTER_CONFIG_SYNC = new ResourceLocation(ID, "after_config_sync");

	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
			ConfigTracker.INSTANCE.loadConfigs(ConfigType.SERVER, getServerConfigPath(server));
		});
		ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
			ConfigTracker.INSTANCE.unloadConfigs(ConfigType.SERVER, getServerConfigPath(server));
		});

		ServerPlayConnectionEvents.JOIN.addPhaseOrdering(CONFIG_SYNC, AFTER_CONFIG_SYNC);
		ServerPlayConnectionEvents.JOIN.register(CONFIG_SYNC, (handler, sender, server) -> {
			ServerPlayer player = handler.player;
			if (server.isSingleplayerOwner(player.getGameProfile()))
				return; // don't sync to self

			ConfigTracker.INSTANCE.configSets().get(ConfigType.SERVER).forEach(config -> {
				try {
					String name = config.getFileName();
					byte[] data = Files.readAllBytes(config.getFullPath());

					FriendlyByteBuf buf = PacketByteBufs.create();
					buf.writeUtf(name);
					buf.writeByteArray(data);
					sender.sendPacket(CONFIG_SYNC, buf);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
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
