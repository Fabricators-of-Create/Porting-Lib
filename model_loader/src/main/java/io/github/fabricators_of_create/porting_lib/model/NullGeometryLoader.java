package io.github.fabricators_of_create.porting_lib.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import io.github.fabricators_of_create.porting_lib.model.geometry.IGeometryLoader;

/**
 * A model loader that loads no models.
 */
public class NullGeometryLoader implements IGeometryLoader<EmptyModel> {
	public static final NullGeometryLoader INSTANCE = new NullGeometryLoader();

	@Override
	public EmptyModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
		return null;
	}
}
