package io.github.fabricators_of_create.porting_lib.core.util;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.thread.BlockableEventLoop;

public class PortingLibProxy {
	// I don't like this solution, but I'm too lazy to rewrite it
	public static final PortingLibProxy INSTANCE = instantiate();

	private static PortingLibProxy instantiate() {
		return switch (FabricLoader.getInstance().getEnvironmentType()) {
			case CLIENT -> {
				try {
					yield (PortingLibProxy) Class.forName("io.github.fabricators_of_create.porting_lib.core.client.PortingLibClientProxy").getConstructor().newInstance();
				} catch (ReflectiveOperationException e) {
					throw new RuntimeException("Failed to instantiate client proxy", e);
				}
			}
			case SERVER -> new PortingLibProxy();
		};
	}

	public BlockableEventLoop<Runnable> getClientExecutor() {
		throw new UnsupportedOperationException("Cannot access client on the server");
	}
}
