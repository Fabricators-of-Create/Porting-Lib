package io.github.fabricators_of_create.porting_lib.config.network.configuration;

import java.util.function.Consumer;

import io.github.fabricators_of_create.porting_lib.config.network.ConfigSync;
import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.fabricmc.fabric.api.networking.v1.FabricServerConfigurationNetworkHandler;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ConfigurationTask;
import org.jetbrains.annotations.ApiStatus;

/**
 * Configuration task that syncs the config files to the client
 *
 * @param listener the listener to indicate to that the task is complete
 */
@ApiStatus.Internal
public record SyncConfig(FabricServerConfigurationNetworkHandler listener) implements ConfigurationTask {
	private static final ResourceLocation ID = PortingLib.id("sync_config");
	public static ConfigurationTask.Type TYPE = new ConfigurationTask.Type(ID.toString());

	public void run(Consumer<CustomPacketPayload> sender) {
		ConfigSync.syncConfigs().forEach(sender);
		listener().completeTask(type());
	}

	@Override
	public void start(Consumer<Packet<?>> sender) {
		run(customPacketPayload -> sender.accept(new ClientboundCustomPayloadPacket(customPacketPayload)));
	}

	@Override
	public ConfigurationTask.Type type() {
		return TYPE;
	}
}
