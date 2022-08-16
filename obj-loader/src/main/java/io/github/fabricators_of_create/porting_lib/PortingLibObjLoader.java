package io.github.fabricators_of_create.porting_lib;

import io.github.fabricators_of_create.porting_lib.model.ModelLoaderRegistry;
import io.github.fabricators_of_create.porting_lib.model.obj.OBJLoader;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.resources.ResourceLocation;

public class PortingLibObjLoader implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModelLoaderRegistry.registerLoader(new ResourceLocation("forge","obj"), OBJLoader.INSTANCE);
	}
}
