package io.github.fabricators_of_create.porting_lib.models;

import io.github.fabricators_of_create.porting_lib.PortingConstants;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.UnbakedModel;

public class PortingLibModels implements ModInitializer {
	@Override
	public void onInitialize() {
		ModelLoadingRegistry.INSTANCE.registerResourceProvider(manager -> PortingLibModelLoadingRegistry.INSTANCE);
		PortingLibModelLoadingRegistry.LOADERS.put(PortingConstants.id("composite"), CompositeModelLoader.INSTANCE);
	}
}
