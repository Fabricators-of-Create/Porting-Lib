package io.github.fabricators_of_create.porting_lib.entity;

import io.github.fabricators_of_create.porting_lib.PortingLib;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.Identifier;

/**
 * An interface to implement onto Entities to have custom data synced on spawn.
 * To use, implement the required methods, and have your getAddEntityPacket return createExtraDataSpawnPacket(this).
 */
public interface ExtraSpawnDataEntity {
	void readSpawnData(PacketByteBuf buf);

	void writeSpawnData(PacketByteBuf buf);

	static Packet<?> createExtraDataSpawnPacket(Entity entity) {
		if (entity instanceof ExtraSpawnDataEntity extra) {
			return createExtraDataSpawnPacket(extra, new EntitySpawnS2CPacket(entity));
		} else {
			throw new RuntimeException("can only use createExtraDataSpawnPacket on a ExtraSpawnDataEntity!");
		}
	}

	static Packet<?> createExtraDataSpawnPacket(ExtraSpawnDataEntity entity, EntitySpawnS2CPacket basePacket) {
		PacketByteBuf buf = PacketByteBufs.create();
		basePacket.write(buf);
		entity.writeSpawnData(buf);
		return ServerPlayNetworking.createS2CPacket(EXTRA_DATA_ENTITY_SPAWN, buf);
	}

	Identifier EXTRA_DATA_ENTITY_SPAWN = PortingLib.id("extra_data_entity_spawn");

	@Environment(EnvType.CLIENT)
	static void initClientNetworking() {
		ClientPlayNetworking.registerGlobalReceiver(EXTRA_DATA_ENTITY_SPAWN, ExtraSpawnDataEntity::handlePacketReceived);
	}

	@Environment(EnvType.CLIENT)
	private static void handlePacketReceived(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
		PacketByteBuf copy = PacketByteBufs.copy(buf); // copy so it survives client.execute()
		client.execute(() -> {
			EntitySpawnS2CPacket spawnPacket = new EntitySpawnS2CPacket(copy);
			int entityId = spawnPacket.getId();
			handler.onEntitySpawn(spawnPacket);
			Entity entity = client.world.getEntityById(entityId);
			if (entity instanceof ExtraSpawnDataEntity extra) {
				extra.readSpawnData(copy);
			} else {
				PortingLib.LOGGER.error("ExtraSpawnDataEntity spawn data received, but no corresponding entity was found! Entity: [{}]", entity);
			}
			copy.release();
		});
	}
}
