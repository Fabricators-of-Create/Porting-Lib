package io.github.fabricators_of_create.porting_lib.config.client;

import io.github.fabricators_of_create.porting_lib.config.ConfigRegistry;
import io.github.fabricators_of_create.porting_lib.config.ModConfig;
import io.github.fabricators_of_create.porting_lib.config.PortingLibConfig;
import io.github.fabricators_of_create.porting_lib.config.network.ConfigSync;
import io.github.fabricators_of_create.porting_lib.config.network.payload.ConfigFilePayload;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;

public class PortingLibConfigClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientConfigurationNetworking.registerGlobalReceiver(ConfigFilePayload.TYPE, (payload, context) -> {
			ConfigSync.receiveSyncedConfig(payload.contents(), payload.fileName());
		});
		ConfigRegistry.registerConfig(PortingLibConfig.ID, ModConfig.Type.CLIENT, PortingLibClientConfig.clientSpec);
	}
}
