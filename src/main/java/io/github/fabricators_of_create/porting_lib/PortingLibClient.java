package io.github.fabricators_of_create.porting_lib;

import io.github.fabricators_of_create.porting_lib.entity.ExtraSpawnDataEntity;
import io.github.fabricators_of_create.porting_lib.entity.MultiPartEntity;
import io.github.fabricators_of_create.porting_lib.entity.PartEntity;
import io.github.fabricators_of_create.porting_lib.event.client.ModelsBakedCallback;
import io.github.fabricators_of_create.porting_lib.util.FluidTextUtil;
import io.github.fabricators_of_create.porting_lib.util.FluidVariantFluidAttributesHandler;
import io.github.fabricators_of_create.porting_lib.util.client.ClientHooks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.minecraft.core.Registry;
import net.minecraft.server.packs.PackType;

public class PortingLibClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(FluidTextUtil.NUMBER_FORMAT);
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

		ModelsBakedCallback.EVENT.register((manager, models, loader) -> {
			Registry.FLUID.forEach(fluid -> {
				ClientHooks.registerFluidVariantsFromAttributes(fluid, fluid.getAttributes());
				if (FluidVariantAttributes.getHandler(fluid) == null)
					FluidVariantAttributes.register(fluid, new FluidVariantFluidAttributesHandler(fluid.getAttributes()));
			});
		});
	}
}
