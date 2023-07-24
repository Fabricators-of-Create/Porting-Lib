package io.github.fabricators_of_create.porting_lib.models;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import io.github.fabricators_of_create.porting_lib.models.geometry.GeometryLoaderManager;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelResolver;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.server.packs.resources.Resource;

import net.minecraft.util.GsonHelper;

import org.jetbrains.annotations.Nullable;

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
	public void onInitializeModelLoader(Context ctx) {
		ctx.resolveModel().register(Resolver.INSTANCE);
	}

	/**
	 * Get a reader for the model associated with the provided ID.
	 * The provided ID is converted from model format (minecraft:block/stone) to file format (minecraft:models/block/stone.json)
	 * @throws IOException on an IO error
	 * @throws FileNotFoundException when the model's resource does not exist
	 */
	public static BufferedReader getModelJson(ResourceLocation location) throws IOException {
		ResourceLocation file = new ResourceLocation(location.getNamespace(), "models/" + location.getPath() + ".json");
		Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(file);
		return resource.orElseThrow(() -> new FileNotFoundException(file.toString())).openAsReader();
	}

	private enum Resolver implements ModelResolver {
		INSTANCE;

		@Override
		@Nullable
		public UnbakedModel resolveModel(Context context) {
			ResourceLocation modelId = context.id();
			if (modelId.getPath().contains("builtin"))
				return null;
			try {
				JsonObject object = GSON.fromJson(getModelJson(modelId), JsonObject.class);
				String loader = GeometryLoaderManager.getModelLoader(object);
				if (loader != null) {
					ResourceLocation id = new ResourceLocation(loader);
					if (!LOADERS.containsKey(id))
						return null;
					BlockModel ownerModel = BlockModel.fromString(object.toString());
					return LOADERS.get(id).readModel(ownerModel, object);
				}
			} catch (Exception e) {
				PortingLib.LOGGER.error("Unhandled error loading model: " + modelId, e);
			}
			return null;
		}
	}
}
