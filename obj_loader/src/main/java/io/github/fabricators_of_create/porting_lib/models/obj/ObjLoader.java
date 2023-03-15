package io.github.fabricators_of_create.porting_lib.models.obj;

import java.io.FileNotFoundException;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import io.github.fabricators_of_create.porting_lib.PortingConstants;
import io.github.fabricators_of_create.porting_lib.models.geometry.IGeometryLoader;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.GsonHelper;

import org.jetbrains.annotations.Nullable;

/**
 * A loader for {@link ObjModel OBJ models}.
 * <p>
 * Allows the user to enable automatic face culling, toggle quad shading, flip UVs, render emissively and specify a
 * {@link ObjMaterialLibrary material library} override.
 */
public class ObjLoader implements IGeometryLoader<ObjModel>, ResourceManagerReloadListener, IdentifiableResourceReloadListener {
	public static ObjLoader INSTANCE = new ObjLoader();

	private final Map<ObjModel.ModelSettings, ObjModel> modelCache = Maps.newConcurrentMap();
	private final Map<ResourceLocation, ObjMaterialLibrary> materialCache = Maps.newConcurrentMap();

	private ResourceManager manager = Minecraft.getInstance().getResourceManager();

	@Override
	public void onResourceManagerReload(ResourceManager resourceManager) {
		modelCache.clear();
		materialCache.clear();
		manager = resourceManager;
	}

	public void setManager(ResourceManager manager) {
		this.manager = manager;
	}

	@Override
	public ObjModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) {
		if (!jsonObject.has("model"))
			throw new JsonParseException("OBJ Loader requires a 'model' key that points to a valid .OBJ model.");

		String modelLocation = jsonObject.get("model").getAsString();

		boolean automaticCulling = GsonHelper.getAsBoolean(jsonObject, "automatic_culling", true);
		boolean shadeQuads = GsonHelper.getAsBoolean(jsonObject, "shade_quads", true);
		boolean flipV = GsonHelper.getAsBoolean(jsonObject, "flip_v", false);
		boolean emissiveAmbient = GsonHelper.getAsBoolean(jsonObject, "emissive_ambient", true);
		String mtlOverride = GsonHelper.getAsString(jsonObject, "mtl_override", null);

		// TODO: Deprecated names. To be removed in 1.20
		var deprecationWarningsBuilder = ImmutableMap.<String, String>builder();
		if (jsonObject.has("detectCullableFaces")) {
			automaticCulling = GsonHelper.getAsBoolean(jsonObject, "detectCullableFaces");
			deprecationWarningsBuilder.put("detectCullableFaces", "automatic_culling");
		}
		if (jsonObject.has("diffuseLighting")) {
			shadeQuads = GsonHelper.getAsBoolean(jsonObject, "diffuseLighting");
			deprecationWarningsBuilder.put("diffuseLighting", "shade_quads");
		}
		if (jsonObject.has("flip-v")) {
			flipV = GsonHelper.getAsBoolean(jsonObject, "flip-v");
			deprecationWarningsBuilder.put("flip-v", "flip_v");
		}
		if (jsonObject.has("ambientToFullbright")) {
			emissiveAmbient = GsonHelper.getAsBoolean(jsonObject, "ambientToFullbright");
			deprecationWarningsBuilder.put("ambientToFullbright", "emissive_ambient");
		}
		if (jsonObject.has("materialLibraryOverride")) {
			mtlOverride = GsonHelper.getAsString(jsonObject, "materialLibraryOverride");
			deprecationWarningsBuilder.put("materialLibraryOverride", "mtl_override");
		}

		return loadModel(new ObjModel.ModelSettings(new ResourceLocation(modelLocation), automaticCulling, shadeQuads, flipV, emissiveAmbient, mtlOverride), deprecationWarningsBuilder.build());
	}

	public ObjModel loadModel(ObjModel.ModelSettings settings) {
		return loadModel(settings, Map.of());
	}

	public ObjModel loadModel(Resource resource, ObjModel.ModelSettings settings) {
		return loadModel(resource, settings, Map.of());
	}

	private ObjModel loadModel(ObjModel.ModelSettings settings, Map<String, String> deprecationWarnings) {
		return modelCache.computeIfAbsent(settings, (data) -> {
			Resource resource = manager.getResource(settings.modelLocation()).orElseThrow();
			try (ObjTokenizer tokenizer = new ObjTokenizer(resource.open())) {
				return ObjModel.parse(tokenizer, settings, deprecationWarnings);
			} catch (FileNotFoundException e) {
				throw new RuntimeException("Could not find OBJ model", e);
			} catch (Exception e) {
				throw new RuntimeException("Could not read OBJ model", e);
			}
		});
	}

	private ObjModel loadModel(Resource resource, ObjModel.ModelSettings settings, Map<String, String> deprecationWarnings) {
		return modelCache.computeIfAbsent(settings, (data) -> {
			try (ObjTokenizer tokenizer = new ObjTokenizer(resource.open())) {
				return ObjModel.parse(tokenizer, settings, deprecationWarnings);
			} catch (FileNotFoundException e) {
				throw new RuntimeException("Could not find OBJ model", e);
			} catch (Exception e) {
				throw new RuntimeException("Could not read OBJ model", e);
			}
		});
	}

	public ObjMaterialLibrary loadMaterialLibrary(ResourceLocation materialLocation) {
		return loadMaterialLibrary(materialLocation, null);
	}

	public ObjMaterialLibrary loadMaterialLibrary(ResourceLocation materialLocation, @Nullable ResourceManager resourceManager) {
		return materialCache.computeIfAbsent(materialLocation, (location) -> {
			Resource resource = resourceManager != null ? resourceManager.getResource(location).orElseThrow() : manager.getResource(location).orElseThrow();
			try (ObjTokenizer rdr = new ObjTokenizer(resource.open())) {
				return new ObjMaterialLibrary(rdr);
			} catch (FileNotFoundException e) {
				throw new RuntimeException("Could not find OBJ material library", e);
			} catch (Exception e) {
				throw new RuntimeException("Could not read OBJ material library", e);
			}
		});
	}

	@Override
	public ResourceLocation getFabricId() {
		return PortingConstants.id("obj-loader");
	}
}
