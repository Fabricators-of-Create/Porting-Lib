package io.github.fabricators_of_create.porting_lib.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.google.gson.JsonParseException;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.UnbakedModel;

public enum CompositeModelLoader implements ModelLoader {
	INSTANCE;

	@Override
	public UnbakedModel readModel(BlockModel parent, JsonObject jsonObject) {
		List<String> itemPasses = new ArrayList<>();
		ImmutableMap.Builder<String, BlockModel> childrenBuilder = ImmutableMap.builder();
		readChildren(jsonObject, "children", childrenBuilder, itemPasses, false);
		boolean logWarning = readChildren(jsonObject, "parts", childrenBuilder, itemPasses, true);

		var children = childrenBuilder.build();
		if (children.isEmpty())
			throw new JsonParseException("Composite model requires a \"children\" element with at least one element.");

		if (jsonObject.has("item_render_order")) {
			itemPasses.clear();
			for (var element : jsonObject.getAsJsonArray("item_render_order")) {
				var name = element.getAsString();
				if (!children.containsKey(name))
					throw new JsonParseException("Specified \"" + name + "\" in \"item_render_order\", but that is not a child of this model.");
				itemPasses.add(name);
			}
		}

		return new CompositeModel(parent, children, ImmutableList.copyOf(itemPasses));
	}

	public boolean readChildren(JsonObject jsonObject, String name, ImmutableMap.Builder<String, BlockModel> children, List<String> itemPasses, boolean logWarning) {
		if (!jsonObject.has(name))
			return false;
		var childrenJsonObject = jsonObject.getAsJsonObject(name);
		for (Map.Entry<String, JsonElement> entry : childrenJsonObject.entrySet()) {
			children.put(entry.getKey(), PortingLibModelLoadingRegistry.GSON.fromJson(entry.getValue(), BlockModel.class));
			itemPasses.add(entry.getKey()); // We can do this because GSON preserves ordering during deserialization
		}
		return logWarning;
	}
}
