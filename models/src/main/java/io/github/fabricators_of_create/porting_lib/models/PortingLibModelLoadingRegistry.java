package io.github.fabricators_of_create.porting_lib.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

import com.google.gson.JsonSyntaxException;

import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.server.packs.resources.Resource;

import net.minecraft.util.GsonHelper;

import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum PortingLibModelLoadingRegistry implements ModelResourceProvider {
	INSTANCE;

	public static final Map<ResourceLocation, ModelLoader> LOADERS = new HashMap<>();

	public static final Gson GSON = BlockModel.GSON.newBuilder().registerTypeAdapter(RenderMaterial.class, new RenderMaterialDeserializer()).create();

	@Override
	public @Nullable UnbakedModel loadModelResource(ResourceLocation resourceId, ModelProviderContext context) {
		try {
			JsonObject object = GSON.fromJson(getModelJson(resourceId), JsonObject.class);
			if (object.has("loader")) {
				ResourceLocation id = new ResourceLocation(GsonHelper.getAsString(object, "loader"));
				if (!LOADERS.containsKey(id))
					return null;
				BlockModel ownerModel = BlockModel.fromString(object.toString());
				return LOADERS.get(id).readModel(ownerModel, object);
			}
		} catch (Exception exception) {
			return null;
		}

		return null;
	}

	public static BufferedReader getModelJson(ResourceLocation location) {
		ResourceLocation file = new ResourceLocation(location.getNamespace(), "models/" + location.getPath() + ".json");
		Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(file);
		try {
			return resource.get().openAsReader();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
