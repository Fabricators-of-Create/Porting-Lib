package io.github.fabricators_of_create.porting_lib.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import io.github.fabricators_of_create.porting_lib.models.geometry.IGeometryLoader;
import net.minecraft.client.renderer.block.model.BlockModel;

public enum CompositeModelLoader implements IGeometryLoader<CompositeModel> {
	INSTANCE;

	@Override
	public CompositeModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) {
		List<String> itemPasses = new ArrayList<>();
		ImmutableMap.Builder<String, BlockModel> childrenBuilder = ImmutableMap.builder();
		readChildren(jsonObject, "children", deserializationContext, childrenBuilder, itemPasses);

		var children = childrenBuilder.build();
		if (children.isEmpty()) {
			throw new JsonParseException("Composite model requires a \"children\" element with at least one element.");
		}

		if (jsonObject.has("item_render_order")) {
			itemPasses.clear();
			for (var element : jsonObject.getAsJsonArray("item_render_order")) {
				var name = element.getAsString();
				if (!children.containsKey(name)) {
					throw new JsonParseException("Specified \"" + name + "\" in \"item_render_order\", but that is not a child of this model.");
				}
				itemPasses.add(name);
			}
		}

		return new CompositeModel(children, ImmutableList.copyOf(itemPasses));
	}

	private void readChildren(JsonObject jsonObject, String name, JsonDeserializationContext deserializationContext, ImmutableMap.Builder<String, BlockModel> children, List<String> itemPasses) {
		if (!jsonObject.has(name)) {
			return;
		}
		var childrenJsonObject = jsonObject.getAsJsonObject(name);
		for (Map.Entry<String, JsonElement> entry : childrenJsonObject.entrySet()) {
			children.put(entry.getKey(), deserializationContext.deserialize(entry.getValue(), BlockModel.class));
			itemPasses.add(entry.getKey()); // We can do this because GSON preserves ordering during deserialization
		}
	}
}
