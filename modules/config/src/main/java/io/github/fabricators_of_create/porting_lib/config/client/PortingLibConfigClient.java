package io.github.fabricators_of_create.porting_lib.config.client;

import io.github.fabricators_of_create.porting_lib.config.ConfigTracker;
import io.github.fabricators_of_create.porting_lib.config.ModConfig;
import io.github.fabricators_of_create.porting_lib.config.PortingLibConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PortingLibConfigClient implements ClientModInitializer {
	private static final Logger logger = LoggerFactory.getLogger(PortingLibConfigClient.class);

	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(PortingLibConfig.CONFIG_SYNC, (client, handler, buf, sender) -> {
			String name = buf.readUtf();
			byte[] data = buf.readByteArray();
			ModConfig config = ConfigTracker.INSTANCE.fileMap().get(name);
			if (config != null) {
				config.acceptSyncedConfig(data);
			} else {
				logger.warn("Received config data for unknown file {}", name);
			}
		});
	}
}
