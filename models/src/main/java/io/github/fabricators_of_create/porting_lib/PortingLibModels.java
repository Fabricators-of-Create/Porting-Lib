package io.github.fabricators_of_create.porting_lib;

import io.github.fabricators_of_create.porting_lib.model_loader.event.client.RegisterGeometryLoadersCallback;
import io.github.fabricators_of_create.porting_lib.model_loader.model.CompositeModel;
import io.github.fabricators_of_create.porting_lib.model_loader.model.DynamicFluidContainerModel;
import io.github.fabricators_of_create.porting_lib.model_loader.model.ElementsModel;
import io.github.fabricators_of_create.porting_lib.model_loader.model.ItemLayerModel;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.resources.ResourceLocation;

public class PortingLibModels implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		RegisterGeometryLoadersCallback.EVENT.register(loaders -> {
			loaders.put(new ResourceLocation("forge", "elements"), ElementsModel.Loader.INSTANCE);
			loaders.put(new ResourceLocation("minecraft:elements"), ElementsModel.Loader.INSTANCE_DEPRECATED); // TODO: Deprecated. To be removed in 1.20
			loaders.put(new ResourceLocation("forge","composite"), CompositeModel.Loader.INSTANCE);
			loaders.put(new ResourceLocation("forge", "item_layers"), ItemLayerModel.Loader.INSTANCE);
			loaders.put(new ResourceLocation("forge","item-layers"), ItemLayerModel.Loader.INSTANCE_DEPRECATED);
			loaders.put(new ResourceLocation("forge", "fluid_container"), DynamicFluidContainerModel.Loader.INSTANCE);
			loaders.put(new ResourceLocation("forge", "bucket"), DynamicFluidContainerModel.Loader.INSTANCE_DEPRECATED);
		});
	}
}
