package io.github.fabricators_of_create.porting_lib.config.client;

import io.github.fabricators_of_create.porting_lib.config.network.ConfigSync;
import io.github.fabricators_of_create.porting_lib.config.network.payload.ConfigFilePayload;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class PortingLibConfigClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(ConfigFilePayload.TYPE, (payload, context) -> {
			ConfigSync.receiveSyncedConfig(payload.contents(), payload.fileName());
		});
	}
}
