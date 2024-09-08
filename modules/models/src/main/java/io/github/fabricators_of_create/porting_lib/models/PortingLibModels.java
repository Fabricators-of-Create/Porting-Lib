package io.github.fabricators_of_create.porting_lib.models;

import com.mojang.math.Transformation;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import io.github.fabricators_of_create.porting_lib.models.geometry.RegisterGeometryLoadersCallback;
import io.github.fabricators_of_create.porting_lib.models.util.TransformationHelper;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.ResourceLocation;

public class PortingLibModels implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModelLoadingPlugin.register(PortingLibModelLoadingRegistry.INSTANCE);

		RegisterGeometryLoadersCallback.EVENT.register(loaders -> {
			loaders.put(PortingLib.id("elements"), ElementsModel.Loader.INSTANCE);

			loaders.put(PortingLib.id("composite"), CompositeModel.Loader.INSTANCE);
			loaders.put(PortingLib.id("item_layers"), ItemLayerModel.Loader.INSTANCE);

//			loaders.put(PortingLib.id("fluid_container"), DynamicFluidContainerModel.Loader.INSTANCE); TODO: PORT

		});
		BlockModel.GSON = BlockModel.GSON.newBuilder()
				.registerTypeAdapter(Transformation.class, new TransformationHelper.Deserializer())
				.create();
	}
}
