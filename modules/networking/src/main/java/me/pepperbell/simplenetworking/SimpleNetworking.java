package me.pepperbell.simplenetworking;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

public class SimpleNetworking implements ModInitializer {
	private static MinecraftServer currentServer;

	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
			currentServer = server;
		});
	}

	public static MinecraftServer getCurrentServer() {
		return currentServer;
	}
}
