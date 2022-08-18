package io.github.fabricators_of_create.porting_lib;

import com.google.common.base.Charsets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;

import io.github.fabricators_of_create.porting_lib.event.client.RegisterGeometryLoadersCallback;
import io.github.fabricators_of_create.porting_lib.model.obj.ObjLoader;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.util.GsonHelper;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.NoSuchElementException;

public class PortingLibObjLoader implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		RegisterGeometryLoadersCallback.EVENT.register(loaders -> loaders.put(new ResourceLocation("forge","obj"), ObjLoader.INSTANCE));
		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(ObjLoader.INSTANCE);
		ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> manager.listResources("models/misc", resourceLocation -> {
			if (resourceLocation.getPath().endsWith(".json")) {
				manager.getResource(resourceLocation).ifPresent(resource -> {
					try {
					JsonObject jsonObject = Streams.parse(new JsonReader(new InputStreamReader(resource.open(), Charsets.UTF_8))).getAsJsonObject();
					if (jsonObject.has(PortingConstants.ID + ":" + "obj_marker")) {
						out.accept(resourceLocation);
					}
					} catch (IOException | NoSuchElementException e) {
						e.fillInStackTrace();
					}
				});
			}
			return true;
		}));
	}
}
