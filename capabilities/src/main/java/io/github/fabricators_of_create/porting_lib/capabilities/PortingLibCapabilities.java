package io.github.fabricators_of_create.porting_lib.capabilities;

import net.fabricmc.api.ModInitializer;

public class PortingLibCapabilities implements ModInitializer {
	@Override
	public void onInitialize() {
		CapabilityManager.INSTANCE.injectCapabilities();
	}
}
