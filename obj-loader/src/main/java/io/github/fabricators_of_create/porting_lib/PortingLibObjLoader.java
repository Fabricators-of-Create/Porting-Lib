package io.github.fabricators_of_create.porting_lib;

import io.github.fabricators_of_create.porting_lib.event.client.RegisterGeometryLoadersCallback;
import io.github.fabricators_of_create.porting_lib.model.obj.ObjLoader;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

public class PortingLibObjLoader implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		RegisterGeometryLoadersCallback.EVENT.register(loaders -> loaders.put(new ResourceLocation("forge","obj"), ObjLoader.INSTANCE));
		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(ObjLoader.INSTANCE);
		ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> manager.listResources("models/misc", resourceLocation -> {
			out.accept(resourceLocation);
			return true;
		}));
	}
}
