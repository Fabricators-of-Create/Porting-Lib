package io.github.fabricators_of_create.porting_lib.model_loader.model.geometry;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableMap;

import io.github.fabricators_of_create.porting_lib.model_loader.event.client.RegisterGeometryLoadersCallback;
import net.minecraft.resources.ResourceLocation;

/**
 * Manager for {@linkplain IGeometryLoader geometry loaders}.
 * <p>
 * Provides a lookup.
 */
public final class GeometryLoaderManager {
	private static ImmutableMap<ResourceLocation, IGeometryLoader<?>> LOADERS;
	private static String LOADER_LIST;

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

	@ApiStatus.Internal
	public static void init() {
		Map<ResourceLocation, IGeometryLoader<?>> loaders = new HashMap<ResourceLocation, IGeometryLoader<?>>();
		RegisterGeometryLoadersCallback.EVENT.invoker().registerGeometryLoaders(loaders);
		LOADERS = ImmutableMap.copyOf(loaders);
		LOADER_LIST = loaders.keySet().stream().map(ResourceLocation::toString).collect(Collectors.joining(", "));
	}

	private GeometryLoaderManager() {}
}
