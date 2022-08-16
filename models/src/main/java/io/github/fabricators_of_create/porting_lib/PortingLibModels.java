package io.github.fabricators_of_create.porting_lib;

import io.github.fabricators_of_create.porting_lib.model.CompositeModel;
import io.github.fabricators_of_create.porting_lib.model.ItemLayerModel;
import io.github.fabricators_of_create.porting_lib.model.ModelLoaderRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.resources.ResourceLocation;

public class PortingLibModels implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModelLoaderRegistry.registerLoader(new ResourceLocation("forge","composite"), CompositeModel.Loader.INSTANCE);
		ModelLoaderRegistry.registerLoader(new ResourceLocation("forge","item-layers"), ItemLayerModel.Loader.INSTANCE);
	}
}
