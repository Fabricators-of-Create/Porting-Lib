package io.github.fabricators_of_create.porting_lib.transfer.client;

import io.github.fabricators_of_create.porting_lib.transfer.client.internal.cache.ClientBlockApiCache;
import net.fabricmc.api.ClientModInitializer;

public class PortingLibTransferClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientBlockApiCache.init();
	}
}
