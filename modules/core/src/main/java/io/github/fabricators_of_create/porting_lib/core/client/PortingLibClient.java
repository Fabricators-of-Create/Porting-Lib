package io.github.fabricators_of_create.porting_lib.core.client;

import io.github.fabricators_of_create.porting_lib.core.util.LogicalSidedProvider;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

public class PortingLibClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientLifecycleEvents.CLIENT_STARTED.register(client -> LogicalSidedProvider.setClient(() -> client));
	}
}
