package io.github.fabricators_of_create.porting_lib.entity;

import io.github.fabricators_of_create.porting_lib.PortingLib;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

/**
 * An interface to implement onto Entities to have custom data synced on spawn.
 * To use, implement the required methods, and have your getAddEntityPacket return createExtraDataSpawnPacket(this).
 */
public interface ExtraSpawnDataEntity {
	void readSpawnData(FriendlyByteBuf buf);

	void writeSpawnData(FriendlyByteBuf buf);

	static Packet<?> createExtraDataSpawnPacket(Entity entity) {
		if (entity instanceof ExtraSpawnDataEntity extra) {
			return createExtraDataSpawnPacket(extra, new ClientboundAddEntityPacket(entity));
		} else {
			throw new RuntimeException("can only use createExtraDataSpawnPacket on a ExtraSpawnDataEntity!");
		}
	}

	static Packet<?> createExtraDataSpawnPacket(ExtraSpawnDataEntity entity, ClientboundAddEntityPacket basePacket) {
		FriendlyByteBuf buf = PacketByteBufs.create();
		basePacket.write(buf);
		entity.writeSpawnData(buf);
		return ServerPlayNetworking.createS2CPacket(EXTRA_DATA_ENTITY_SPAWN, buf);
	}

	ResourceLocation EXTRA_DATA_ENTITY_SPAWN = PortingLib.id("extra_data_entity_spawn");

	@Environment(EnvType.CLIENT)
	static void initClientNetworking() {
		ClientPlayNetworking.registerGlobalReceiver(EXTRA_DATA_ENTITY_SPAWN, ExtraSpawnDataEntity::handlePacketReceived);
	}

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
}
