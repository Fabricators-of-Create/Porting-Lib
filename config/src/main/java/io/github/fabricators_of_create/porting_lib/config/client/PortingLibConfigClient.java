package io.github.fabricators_of_create.porting_lib.config.client;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import io.github.fabricators_of_create.porting_lib.config.ConfigTracker;
import io.github.fabricators_of_create.porting_lib.config.PortingLibConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.minecraft.client.Minecraft;

public class PortingLibConfigClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientLoginNetworking.registerGlobalReceiver(PortingLibConfig.CONFIG_SYNC, (client, handler, buf, listenerAdder) -> {
			if (!Minecraft.getInstance().isLocalServer()) {
				String fileName = buf.readUtf();
				byte[] bytes = buf.readByteArray();
				Optional.ofNullable(ConfigTracker.INSTANCE.fileMap().get(fileName)).ifPresent(mc-> mc.acceptSyncedConfig(bytes));
			}
			return CompletableFuture.completedFuture(null);
		});
	}
}
