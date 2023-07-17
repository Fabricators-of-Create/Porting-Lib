package io.github.fabricators_of_create.porting_lib.models;

import com.mojang.math.Transformation;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import io.github.fabricators_of_create.porting_lib.models.geometry.RegisterGeometryLoadersCallback;
import io.github.fabricators_of_create.porting_lib.models.util.TransformationHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.ResourceLocation;

public class PortingLibModels implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModelLoadingRegistry.INSTANCE.registerResourceProvider(manager -> PortingLibModelLoadingRegistry.INSTANCE);

		PortingLibModelLoadingRegistry.LOADERS.put(PortingLib.id("item_layers"), ItemLayerModel.Loader.INSTANCE);

		RegisterGeometryLoadersCallback.EVENT.register(loaders -> {
			loaders.put(PortingLib.id("elements"), ElementsModel.Loader.INSTANCE);

			loaders.put(PortingLib.id("composite"), CompositeModelLoader.INSTANCE);

			loaders.put(PortingLib.id("fluid_container"), DynamicFluidContainerModel.Loader.INSTANCE);

			loaders.put(new ResourceLocation("forge", "bucket"), DynamicFluidContainerModel.Loader.INSTANCE_DEPRECATED);
		});
		BlockModel.GSON = BlockModel.GSON.newBuilder()
				.registerTypeAdapter(Transformation.class, new TransformationHelper.Deserializer())
				.create();
	}
}
