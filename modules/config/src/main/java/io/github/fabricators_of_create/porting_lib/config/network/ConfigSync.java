package io.github.fabricators_of_create.porting_lib.config.network;

import io.github.fabricators_of_create.porting_lib.config.ConfigTracker;
import io.github.fabricators_of_create.porting_lib.config.ModConfig;
import io.github.fabricators_of_create.porting_lib.config.ModConfigs;
import io.github.fabricators_of_create.porting_lib.config.network.payload.ConfigFilePayload;
import net.minecraft.client.Minecraft;

import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@ApiStatus.Internal
public final class ConfigSync {
	private ConfigSync() {}

	public static List<ConfigFilePayload> syncConfigs() {
		final Map<String, byte[]> configData = ModConfigs.getConfigSet(ModConfig.Type.SERVER).stream().collect(Collectors.toMap(ModConfig::getFileName, mc -> {
			try {
				return Files.readAllBytes(mc.getFullPath());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}));

		return configData.entrySet().stream()
				.map(e -> new ConfigFilePayload(e.getKey(), e.getValue()))
				.toList();
	}

	public static void receiveSyncedConfig(final byte[] contents, final String fileName) {
		if (!Minecraft.getInstance().isLocalServer()) {
			Optional.ofNullable(ModConfigs.getFileMap().get(fileName)).ifPresent(mc -> ConfigTracker.INSTANCE.acceptSyncedConfig(mc, contents));
		}
	}
}
