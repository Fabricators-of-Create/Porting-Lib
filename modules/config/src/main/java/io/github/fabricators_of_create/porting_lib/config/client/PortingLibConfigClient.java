package io.github.fabricators_of_create.porting_lib.config.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.fabricators_of_create.porting_lib.config.ConfigTracker;
import io.github.fabricators_of_create.porting_lib.config.ModConfig;
import io.github.fabricators_of_create.porting_lib.config.network.ConfigSyncPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class PortingLibConfigClient implements ClientModInitializer {
	private static final Logger logger = LoggerFactory.getLogger(PortingLibConfigClient.class);

	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(ConfigSyncPacket.TYPE, (payload, context) -> {
			ModConfig config = ConfigTracker.INSTANCE.fileMap().get(payload.name());
			if (config != null) {
				config.acceptSyncedConfig(payload.data());
			} else {
				logger.warn("Received config data for unknown file {}", payload.name());
			}
		});
	}
}
