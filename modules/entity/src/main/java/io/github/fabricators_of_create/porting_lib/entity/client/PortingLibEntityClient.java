package io.github.fabricators_of_create.porting_lib.entity.client;

import java.util.Objects;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import io.github.fabricators_of_create.porting_lib.entity.IEntityAdditionalSpawnData;
import io.github.fabricators_of_create.porting_lib.entity.MultiPartEntity;
import io.github.fabricators_of_create.porting_lib.entity.PartEntity;
import io.github.fabricators_of_create.porting_lib.entity.mixin.common.BlockableEventLoopAccessor;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;

public class PortingLibEntityClient implements ClientModInitializer {
	@Environment(EnvType.CLIENT)
	private static void handlePacketReceived(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
		int entityId = buf.readVarInt();
		buf.retain(); // save for execute
		execute(client, () -> {
			Entity entity = Objects.requireNonNull(client.level).getEntity(entityId);
			if (entity instanceof IEntityAdditionalSpawnData extra) {
				extra.readSpawnData(buf);
			} else {
				PortingLib.LOGGER.error("ExtraSpawnDataEntity spawn data received, but no corresponding entity was found! Entity: [{}]", entity);
			}
			buf.release();
		});
	}

	private static void execute(Minecraft mc, Runnable task) {
		// sometimes MC will defer a task even if already on the right thread.
		// Forge avoids this when calling enqueueWork.
		// We need to replicate this behavior for proper packet ordering.
		if (mc.isSameThread()) {
			task.run();
		} else {
			// Forge also bypasses public methods
			((BlockableEventLoopAccessor) mc).callSubmitAsync(task);
		}
	}

	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(IEntityAdditionalSpawnData.EXTRA_DATA_PACKET, PortingLibEntityClient::handlePacketReceived);
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
