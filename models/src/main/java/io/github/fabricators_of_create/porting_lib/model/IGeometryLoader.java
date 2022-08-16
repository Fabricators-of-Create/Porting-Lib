package io.github.fabricators_of_create.porting_lib.model;


import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

/**
 * A loader for custom {@linkplain IUnbakedGeometry model geometries}.
 * <p>
 * If you do any caching, you should implement {@link ResourceManagerReloadListener} and register it with
 * {@link RegisterClientReloadListenersEvent}.
 *
 * @see RegisterGeometryLoaders
 * @see RegisterClientReloadListenersEvent
 */
public interface IGeometryLoader<T extends IUnbakedGeometry<T>> {
	T read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException;
}
