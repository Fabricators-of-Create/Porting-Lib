package io.github.fabricators_of_create.porting_lib.model.obj;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import io.github.fabricators_of_create.porting_lib.model.IModelLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import java.util.*;

public class OBJLoader implements IModelLoader<OBJModel>
{
	public static OBJLoader INSTANCE = new OBJLoader();

	private final Map<OBJModel.ModelSettings, OBJModel> modelCache = Maps.newHashMap();
	private final Map<ResourceLocation, MaterialLibrary> materialCache = Maps.newHashMap();

	private ResourceManager manager = Minecraft.getInstance().getResourceManager();

	@Override
	public void onResourceManagerReload(ResourceManager resourceManager)
	{
		modelCache.clear();
		materialCache.clear();
		manager = resourceManager;
	}

	@Override
	public OBJModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents)
	{
		if (!modelContents.has("model"))
			throw new RuntimeException("OBJ Loader requires a 'model' key that points to a valid .OBJ model.");

		String modelLocation = modelContents.get("model").getAsString();

		boolean detectCullableFaces = GsonHelper.getAsBoolean(modelContents, "detectCullableFaces", true);
		boolean diffuseLighting = GsonHelper.getAsBoolean(modelContents, "diffuseLighting", false);
		boolean flipV = GsonHelper.getAsBoolean(modelContents, "flip-v", false);
		boolean ambientToFullbright = GsonHelper.getAsBoolean(modelContents, "ambientToFullbright", true);
		@Nullable
		String materialLibraryOverrideLocation = modelContents.has("materialLibraryOverride") ? GsonHelper.getAsString(modelContents, "materialLibraryOverride") : null;

		return loadModel(new OBJModel.ModelSettings(new ResourceLocation(modelLocation), detectCullableFaces, diffuseLighting, flipV, ambientToFullbright, materialLibraryOverrideLocation));
	}

	public OBJModel loadModel(OBJModel.ModelSettings settings)
	{
		return modelCache.computeIfAbsent(settings, (data) -> {
			Resource resource = manager.getResource(settings.modelLocation()).orElseThrow();
			try(LineReader rdr = new LineReader(resource)) {
				return new OBJModel(rdr, settings);
			} catch (FileNotFoundException e) {
				throw new RuntimeException("Could not find OBJ model", e);
			} catch (Exception e) {
				throw new RuntimeException("Could not read OBJ model", e);
			}
		});
	}

	public MaterialLibrary loadMaterialLibrary(ResourceLocation materialLocation) {
		return materialCache.computeIfAbsent(materialLocation, (location) -> {
			Resource resource = manager.getResource(location).orElseThrow();
			try(LineReader rdr = new LineReader(resource)) {
				return new MaterialLibrary(rdr);
			} catch (FileNotFoundException e) {
				throw new RuntimeException("Could not find OBJ material library", e);
			} catch (Exception e) {
				throw new RuntimeException("Could not read OBJ material library", e);
			}
		});
	}
}
