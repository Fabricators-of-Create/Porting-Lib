package xyz.bluspring.forgecapabilities.capabilities;

import net.fabricmc.api.ModInitializer;

public class PortingLibCapabilities implements ModInitializer {
	@Override
	public void onInitialize() {
		CapabilityManager.INSTANCE.injectCapabilities();
	}
}
