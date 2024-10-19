package io.github.fabricators_of_create.porting_lib.models.testmod.client;

import io.github.fabricators_of_create.porting_lib.models.DynamicFluidContainerModel;
import io.github.fabricators_of_create.porting_lib.models.testmod.PortingLibModelsTestmod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;

import static io.github.fabricators_of_create.porting_lib.models.testmod.PortingLibModelsTestmod.WATER_BUCKET;

public class PortingLibModelsTestmodClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModelResourceLocation location = new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(PortingLibModelsTestmod.DERPY_HELMET), "inventory");
		ModelLoadingPlugin.register(pluginCtx -> pluginCtx.modifyModelAfterBake().register(
				(model, ctx) -> ctx.topLevelId().equals(location) ? new DerpyItemModel(model) : model
		));

		ColorProviderRegistry.ITEM.register(new DynamicFluidContainerModel.Colors(), WATER_BUCKET);
	}
}
