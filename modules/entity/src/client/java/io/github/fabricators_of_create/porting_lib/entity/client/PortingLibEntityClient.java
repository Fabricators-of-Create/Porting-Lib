package io.github.fabricators_of_create.porting_lib.entity.client;

import io.github.fabricators_of_create.porting_lib.entity.IEntityWithComplexSpawn;
import io.github.fabricators_of_create.porting_lib.entity.MultiPartEntity;
import io.github.fabricators_of_create.porting_lib.entity.PartEntity;
import io.github.fabricators_of_create.porting_lib.entity.mixin.common.BlockableEventLoopAccessor;
import io.github.fabricators_of_create.porting_lib.entity.network.AdvancedAddEntityPayload;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

public class PortingLibEntityClient implements ClientModInitializer {
	public static void handleAddComplexEntity(AdvancedAddEntityPayload advancedAddEntityPayload, ClientPlayNetworking.Context context) {
		try {
			Entity entity = context.player().level().getEntity(advancedAddEntityPayload.entityId());
			if (entity instanceof IEntityWithComplexSpawn entityAdditionalSpawnData) {
				final RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(advancedAddEntityPayload.customPayload()), entity.registryAccess());
				try {
					execute(context.client(), () -> entityAdditionalSpawnData.readSpawnData(buf));
				} finally {
					buf.release();
				}
			}
		} catch (Throwable t) {
			context.responseSender().disconnect(Component.translatable("porting_lib.network.advanced_add_entity.failed", t.getMessage()));
		}
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
		ClientPlayNetworking.registerGlobalReceiver(AdvancedAddEntityPayload.TYPE, PortingLibEntityClient::handleAddComplexEntity);
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
