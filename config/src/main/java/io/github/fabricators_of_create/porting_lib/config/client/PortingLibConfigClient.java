package io.github.fabricators_of_create.porting_lib.config.client;

import io.github.fabricators_of_create.porting_lib.config.ConfigTracker;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class PortingLibConfigClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPlayConnectionEvents.INIT.register((handler, client) -> {
			ConfigTracker.INSTANCE.loadDefaultServerConfigs();
		});
	}
}
