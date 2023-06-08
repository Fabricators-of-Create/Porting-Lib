package io.github.fabricators_of_create.porting_lib.entity;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.Entity;

public class PortingLibEntity implements ClientModInitializer {
	@Environment(EnvType.CLIENT)
	private static void handlePacketReceived(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
		FriendlyByteBuf copy = PacketByteBufs.copy(buf); // copy so it survives client.execute()
		client.execute(() -> {
			ClientboundAddEntityPacket spawnPacket = new ClientboundAddEntityPacket(copy);
			int entityId = spawnPacket.getId();
			handler.handleAddEntity(spawnPacket);
			Entity entity = client.level.getEntity(entityId);
			if (entity instanceof ExtraSpawnDataEntity extra) {
				extra.readSpawnData(copy);
			} else {
				PortingLib.LOGGER.error("ExtraSpawnDataEntity spawn data received, but no corresponding entity was found! Entity: [{}]", entity);
			}
			copy.release();
		});
	}

	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(ExtraSpawnDataEntity.EXTRA_DATA_PACKET, PortingLibEntity::handlePacketReceived);
	}
}
