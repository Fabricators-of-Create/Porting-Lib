package io.github.fabricators_of_create.porting_lib.entity.client;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import io.github.fabricators_of_create.porting_lib.entity.ExtraSpawnDataEntity;
import io.github.fabricators_of_create.porting_lib.entity.MultiPartEntity;
import io.github.fabricators_of_create.porting_lib.entity.PartEntity;
import io.github.fabricators_of_create.porting_lib.entity.PortingLibEntity;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.Entity;

public class PortingLibEntityClient implements ClientModInitializer {
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
		ClientPlayNetworking.registerGlobalReceiver(ExtraSpawnDataEntity.EXTRA_DATA_PACKET, PortingLibEntityClient::handlePacketReceived);
		ClientEntityEvents.ENTITY_LOAD.register((entity, world) -> {
			if (entity instanceof MultiPartEntity partEntity && partEntity.isMultipartEntity()) {
				for (PartEntity<?> part : partEntity.getParts()) {
					world.getPartEntityMap().put(part.getId(), part);
				}
			}
		});
		ClientEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
			if (entity instanceof MultiPartEntity partEntity && partEntity.isMultipartEntity()) {
				for (PartEntity<?> part : partEntity.getParts()) {
					world.getPartEntityMap().remove(part.getId());
				}
			}
		});
	}
}
