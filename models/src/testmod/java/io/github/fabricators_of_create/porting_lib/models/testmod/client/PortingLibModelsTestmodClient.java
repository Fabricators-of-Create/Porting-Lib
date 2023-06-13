package io.github.fabricators_of_create.porting_lib.models.testmod.client;

import io.github.fabricators_of_create.porting_lib.models.events.ModelEvents;
import io.github.fabricators_of_create.porting_lib.models.testmod.PortingLibModelsTestmod;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;

public class PortingLibModelsTestmodClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModelResourceLocation location = new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(PortingLibModelsTestmod.DERPY_HELMET), "inventory");
		ModelEvents.MODIFY_BAKING_RESULT.register((models, modelBakery) -> {
			models.put(location, new DerpyItemModel(models.get(location)));
		});
	}
}
