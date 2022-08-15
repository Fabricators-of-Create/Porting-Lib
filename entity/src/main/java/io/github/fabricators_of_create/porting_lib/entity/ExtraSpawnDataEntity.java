package io.github.fabricators_of_create.porting_lib.entity;

import io.github.fabricators_of_create.porting_lib.PortingConstants;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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

	ResourceLocation EXTRA_DATA_ENTITY_SPAWN = PortingConstants.id("extra_data_entity_spawn");
}
