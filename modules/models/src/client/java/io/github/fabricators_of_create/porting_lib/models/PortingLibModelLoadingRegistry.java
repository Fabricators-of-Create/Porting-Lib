package io.github.fabricators_of_create.porting_lib.models;

import com.google.gson.Gson;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.server.packs.resources.Resource;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum PortingLibModelLoadingRegistry implements ModelLoadingPlugin {
	INSTANCE;

	public static final Map<ResourceLocation, ModelLoader> LOADERS = new HashMap<>();

	public static final Gson GSON = BlockModel.GSON.newBuilder().registerTypeAdapter(RenderMaterial.class, new RenderMaterialDeserializer()).create();

	@Override
	public void onInitializeModelLoader(Context ctx) {}

	/**
	 * Get a reader for the model associated with the provided ID.
	 * The provided ID is converted from model format (minecraft:block/stone) to file format (minecraft:models/block/stone.json)
	 * @throws IOException on an IO error
	 * @throws FileNotFoundException when the model's resource does not exist
	 */
	public static BufferedReader getModelJson(ResourceLocation location) throws IOException {
		ResourceLocation file = ResourceLocation.fromNamespaceAndPath(location.getNamespace(), "models/" + location.getPath() + ".json");
		Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(file);
		return resource.orElseThrow(() -> new FileNotFoundException(file.toString())).openAsReader();
	}
}
