package io.github.fabricators_of_create.porting_lib.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.resource.SynchronousResourceReloader;

public interface IModelLoader<T extends IModelGeometry<T>> extends SynchronousResourceReloader {
	T read(JsonDeserializationContext deserializationContext, JsonObject modelContents);
}
