package io.github.fabricators_of_create.porting_lib.models;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;

public class PortingLibModels implements ModInitializer {
	@Override
	public void onInitialize() {
		ModelLoadingRegistry.INSTANCE.registerResourceProvider(manager -> PortingLibModelLoadingRegistry.INSTANCE);
		PortingLibModelLoadingRegistry.LOADERS.put(PortingLib.id("composite"), CompositeModelLoader.INSTANCE);
		PortingLibModelLoadingRegistry.LOADERS.put(PortingLib.id("item_layers"), ItemLayerModel.Loader.INSTANCE);
	}
}
