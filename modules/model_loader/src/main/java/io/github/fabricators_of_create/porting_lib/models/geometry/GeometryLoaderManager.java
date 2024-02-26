package io.github.fabricators_of_create.porting_lib.models.geometry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;

import net.minecraft.util.GsonHelper;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableMap;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.CustomValue;
import net.fabricmc.loader.api.metadata.CustomValue.CvType;
import net.minecraft.resources.ResourceLocation;

/**
 * Manager for {@linkplain IGeometryLoader geometry loaders}.
 * <p>
 * Provides a lookup.
 */
public final class GeometryLoaderManager {
	private static ImmutableMap<ResourceLocation, IGeometryLoader<?>> LOADERS;
	private static String LOADER_LIST;
	public static final List<ResourceLocation> KNOWN_MISSING_LOADERS = new ArrayList<>();

	/**
	 * Finds the {@link IGeometryLoader} for a given name, or null if not found.
	 */
	@Nullable
	public static IGeometryLoader<?> get(ResourceLocation name) {
		return LOADERS.get(name);
	}

	/**
	 * Retrieves a comma-separated list of all active loaders, for use in error messages.
	 */
	public static String getLoaderList() {
		return LOADER_LIST;
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

	@ApiStatus.Internal
	public static void init() {
		Map<ResourceLocation, IGeometryLoader<?>> loaders = new HashMap<>();
		RegisterGeometryLoadersCallback.EVENT.invoker().registerGeometryLoaders(loaders);
		getProvidedLoaders(loaders);
		LOADERS = ImmutableMap.copyOf(loaders);
		LOADER_LIST = loaders.keySet().stream().map(ResourceLocation::toString).collect(Collectors.joining(", "));
	}

	/**
	 * Allows mods to let Porting Lib know of model loaders implemented elsewhere, to prevent log spam.
	 * to use, add a field to your FMJ: <pre>
	 *     "custom": {
	 *         "porting_lib:provided_loaders": [
	 *             "my_mod:my_loader",
	 *             "my_mod:my_other_loader"
	 *         ]
	 *     }
	 * </pre>
	 * These IDs will not be considered missing, and Porting Lib will not interfere with models using them.
	 */
	private static void getProvidedLoaders(Map<ResourceLocation, IGeometryLoader<?>> loaders) {
		List<ResourceLocation> providedLoaders = new ArrayList<>();
		for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
			CustomValue provided = mod.getMetadata().getCustomValue("porting_lib:provided_loaders");
			if (provided == null) {
				continue;
			}
			if (provided.getType() != CvType.ARRAY) {
				PortingLib.LOGGER.error("Mod {} specifies provided loaders, but it's not an array! got: {}",
						mod.getMetadata().getName(), provided.getType());
				continue;
			}
			for (CustomValue value : provided.getAsArray()) {
				if (value.getType() != CvType.STRING) {
					PortingLib.LOGGER.error("Mod {} specifies an array of provided loaders, but it contains a non-string! got: {}",
							mod.getMetadata().getName(), value.getType());
					continue;
				}
				String idString = value.getAsString();
				ResourceLocation id = ResourceLocation.tryParse(idString);
				if (id == null) {
					PortingLib.LOGGER.error("Mod {} provides loader {}, which is not a valid ID!",
							mod.getMetadata().getName(), idString);
					continue;
				}
				providedLoaders.add(id);
			}
		}
		if (!providedLoaders.isEmpty()) {
			StringBuilder out = new StringBuilder("Registered %s provided loaders: ".formatted(providedLoaders.size()));
			for (ResourceLocation loader : providedLoaders) {
				out.append(loader).append(", ");
				loaders.put(loader, NullGeometryLoader.INSTANCE);
			}
			PortingLib.LOGGER.info(out.substring(0, out.length() - 2)); // cut off final ", "
		}
	}

	private GeometryLoaderManager() {}

	static {
		init();
	}
}
