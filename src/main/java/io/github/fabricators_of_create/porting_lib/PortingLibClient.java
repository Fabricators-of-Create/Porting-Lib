package io.github.fabricators_of_create.porting_lib;

import io.github.fabricators_of_create.porting_lib.entity.ExtraSpawnDataEntity;
import io.github.fabricators_of_create.porting_lib.entity.MultiPartEntity;
import io.github.fabricators_of_create.porting_lib.entity.PartEntity;
import io.github.fabricators_of_create.porting_lib.event.client.MinecraftTailCallback;
import io.github.fabricators_of_create.porting_lib.extensions.LevelExtensions;
import io.github.fabricators_of_create.porting_lib.model.ModelLoaderRegistry;
import io.github.fabricators_of_create.porting_lib.util.FluidTextUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.packs.PackType;

public class PortingLibClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(FluidTextUtil.NUMBER_FORMAT);
		MinecraftTailCallback.EVENT.register(mc -> ModelLoaderRegistry.init());
		ExtraSpawnDataEntity.initClientNetworking();
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
