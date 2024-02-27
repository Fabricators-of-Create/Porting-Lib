package io.github.fabricators_of_create.porting_lib.entity;

import com.mojang.logging.LogUtils;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;

import net.minecraft.network.protocol.game.ClientboundBundlePacket;

import net.minecraft.world.entity.Entity;

import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.util.List;

public class PortingLibEntity implements ModInitializer {
	public static final Logger LOGGER = LogUtils.getLogger();

	@Override
	public void onInitialize() {
		ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
			if (entity instanceof MultiPartEntity partEntity && partEntity.isMultipartEntity()) {
				PartEntity<?>[] parts = partEntity.getParts();
				if (parts != null) {
					for (PartEntity<?> part : parts) {
						world.getPartEntityMap().put(part.getId(), part);
					}
				}
			}
		});
		ServerEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
			if (entity instanceof MultiPartEntity partEntity && partEntity.isMultipartEntity()) {
				PartEntity<?>[] parts = partEntity.getParts();
				if (parts != null) {
					for (PartEntity<?> part : parts) {
						world.getPartEntityMap().remove(part.getId());
					}
				}
			}
		});
	}

	public static Packet<ClientGamePacketListener> getEntitySpawningPacket(Entity entity) {
		return getEntitySpawningPacket(entity, new ClientboundAddEntityPacket(entity));
	}

	@ApiStatus.Internal
	public static Packet<ClientGamePacketListener> getEntitySpawningPacket(Entity entity, Packet<ClientGamePacketListener> base) {
		if (entity instanceof IEntityAdditionalSpawnData extra) {
			FriendlyByteBuf buf = PacketByteBufs.create();
			buf.writeVarInt(entity.getId());
			extra.writeSpawnData(buf);
			Packet extraPacket = ServerPlayNetworking.createS2CPacket(IEntityAdditionalSpawnData.EXTRA_DATA_PACKET, buf);
			return new ClientboundBundlePacket(List.of(base, extraPacket));
		}
		return base;
	}
}
