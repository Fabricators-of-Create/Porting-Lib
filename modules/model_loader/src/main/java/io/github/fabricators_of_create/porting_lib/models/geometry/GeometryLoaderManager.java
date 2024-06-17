package io.github.fabricators_of_create.porting_lib.models.geometry;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

/**
 * Manager for {@linkplain IGeometryLoader geometry loaders}.
 * <p>
 * Provides a lookup.
 */
public final class GeometryLoaderManager {

	/**
	 * Finds the {@link IGeometryLoader} for a given name, or null if not found.
	 */
	@Nullable
	public static IGeometryLoader<?> get(ResourceLocation name) {
		return convertToV1(io.github.fabricators_of_create.porting_lib.models_v2.geometry.GeometryLoaderManager.get(name));
	}

	/**
	 * Retrieves a comma-separated list of all active loaders, for use in error messages.
	 */
	public static String getLoaderList() {
		return io.github.fabricators_of_create.porting_lib.models_v2.geometry.GeometryLoaderManager.getLoaderList();
	}

	/**
	 * Get the ID of the model loader which should load the given JSON.
	 */
	@Nullable
	public static String getModelLoader(JsonObject json) {
		if (json.has("porting_lib:loader")) {
			return GsonHelper.getAsString(json, "porting_lib:loader");
		} else if (json.has("loader")) {
			return GsonHelper.getAsString(json, "loader");
		} else {
			return null;
		}
	}

	private static IGeometryLoader<?> convertToV1(io.github.fabricators_of_create.porting_lib.models_v2.geometry.IGeometryLoader<?> loader) {
		throw new RuntimeException("TODO");
	}

	public static io.github.fabricators_of_create.porting_lib.models_v2.geometry.IGeometryLoader<?> convertToV2(IGeometryLoader<?> loader) {
		throw new RuntimeException("TODO");
	}

	@ApiStatus.Internal
	public static void init() {
		Map<ResourceLocation, IGeometryLoader<?>> loaders = new HashMap<>();
		RegisterGeometryLoadersCallback.EVENT.invoker().registerGeometryLoaders(loaders);
		loaders.forEach((id, loader) -> {
			io.github.fabricators_of_create.porting_lib.models_v2.geometry.GeometryLoaderManager.registerLoader(id, convertToV2(loader));
		});
	}

	private GeometryLoaderManager() {}

	static {
		init();
	}
}
