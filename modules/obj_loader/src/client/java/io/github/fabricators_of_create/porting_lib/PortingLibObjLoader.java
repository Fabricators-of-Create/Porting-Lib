package io.github.fabricators_of_create.porting_lib;

import io.github.fabricators_of_create.porting_lib.models.geometry.RegisterGeometryLoadersCallback;
import io.github.fabricators_of_create.porting_lib.models.obj.ObjLoader;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;

public class PortingLibObjLoader implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// OBJ loader can be used for either plain models or block model geometry
		ModelLoadingPlugin.register(ObjLoader.INSTANCE);
		RegisterGeometryLoadersCallback.EVENT.register(loaders -> loaders.put(ObjLoader.ID, ObjLoader.INSTANCE));
	}
}
