package io.github.fabricators_of_create.porting_lib.model;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.util.GsonHelper;

import javax.annotation.Nullable;

import java.util.Map;
import java.util.stream.Collectors;

public class ModelLoaderRegistry {
  private static final Map<ResourceLocation, IModelLoader<?>> loaders = Maps.newHashMap();

  public static void registerLoader(ResourceLocation id, IModelLoader<?> loader) {
    synchronized(loaders) {
      loaders.put(id, loader);
      ((ReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener(loader);
    }
  }

  public static IModelGeometry<?> getModel(ResourceLocation loaderId, JsonDeserializationContext deserializationContext, JsonObject data) {
    try {
      if (!loaders.containsKey(loaderId)) {
        throw new IllegalStateException(String.format("Model loader '%s' not found. Registered loaders: %s", loaderId,
          loaders.keySet().stream().map(ResourceLocation::toString).collect(Collectors.joining(", "))));
      }

      IModelLoader<?> loader = loaders.get(loaderId);

      return loader.read(deserializationContext, data);
    }
    catch(Exception e) {
      e.printStackTrace();
      throw e;
    }
  }

  @Nullable
  public static IModelGeometry<?> deserializeGeometry(JsonDeserializationContext deserializationContext, JsonObject object) {
    if (!object.has("loader")) {
      return null;
    }

    ResourceLocation loader = new ResourceLocation(GsonHelper.getAsString(object,"loader"));
    return getModel(loader, deserializationContext, object);
  }
}
