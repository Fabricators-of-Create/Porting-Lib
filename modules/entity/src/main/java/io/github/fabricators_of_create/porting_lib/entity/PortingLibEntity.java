package io.github.fabricators_of_create.porting_lib.entity;

import com.mojang.logging.LogUtils;

import io.github.fabricators_of_create.porting_lib.core.util.LogicalSidedProvider;
import io.github.fabricators_of_create.porting_lib.entity.events.EntityJoinLevelEvent;
import io.github.fabricators_of_create.porting_lib.entity.network.AdvancedAddEntityPayload;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

import net.minecraft.server.TickTask;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import org.slf4j.Logger;

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
		PayloadTypeRegistry.playS2C().register(AdvancedAddEntityPayload.TYPE, AdvancedAddEntityPayload.STREAM_CODEC);
		EntityJoinLevelEvent.EVENT.register(PortingLibEntity::onEntityJoinWorld);
	}

	public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
		Entity entity = event.getEntity();
		if (entity.getClass().equals(ItemEntity.class)) {
			ItemStack stack = ((ItemEntity) entity).getItem();
			Item item = stack.getItem();
			if (item.hasCustomEntity(stack)) {
				Entity newEntity = item.createEntity(event.getLevel(), entity, stack);
				if (newEntity != null) {
					entity.discard();
					event.setCanceled(true);
					var executor = LogicalSidedProvider.WORKQUEUE.get(event.getLevel().isClientSide ? EnvType.CLIENT : EnvType.SERVER);
					executor.tell(new TickTask(0, () -> event.getLevel().addFreshEntity(newEntity)));
				}
			}
		}
	}
}
