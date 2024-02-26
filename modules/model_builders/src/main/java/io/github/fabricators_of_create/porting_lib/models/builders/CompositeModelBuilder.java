package io.github.fabricators_of_create.porting_lib.models.builders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import io.github.fabricators_of_create.porting_lib.data.ExistingFileHelper;
import io.github.fabricators_of_create.porting_lib.models.generators.CustomLoaderBuilder;
import io.github.fabricators_of_create.porting_lib.models.generators.ModelBuilder;

public class CompositeModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T> {
	public static <T extends ModelBuilder<T>> CompositeModelBuilder<T> begin(T parent, ExistingFileHelper existingFileHelper) {
		return new CompositeModelBuilder<>(parent, existingFileHelper);
	}

	private final Map<String, T> childModels = new LinkedHashMap<>();
	private final List<String> itemRenderOrder = new ArrayList<>();

	protected CompositeModelBuilder(T parent, ExistingFileHelper existingFileHelper) {
		super(PortingLib.id("composite"), parent, existingFileHelper);
	}

	public CompositeModelBuilder<T> child(String name, T modelBuilder) {
		Preconditions.checkNotNull(name, "name must not be null");
		Preconditions.checkNotNull(modelBuilder, "modelBuilder must not be null");
		childModels.put(name, modelBuilder);
		itemRenderOrder.add(name);
		return this;
	}

	public CompositeModelBuilder<T> itemRenderOrder(String... names) {
		Preconditions.checkNotNull(names, "names must not be null");
		Preconditions.checkArgument(names.length > 0, "names must contain at least one element");
		for (String name : names)
			if (!childModels.containsKey(name))
				throw new IllegalArgumentException("names contains \"" + name + "\", which is not a child of this model");
		itemRenderOrder.clear();
		itemRenderOrder.addAll(Arrays.asList(names));
		return this;
	}

	@Override
	public JsonObject toJson(JsonObject json) {
		json = super.toJson(json);

		JsonObject children = new JsonObject();
		for(Map.Entry<String, T> entry : childModels.entrySet()) {
			children.add(entry.getKey(), entry.getValue().toJson());
		}
		json.add("children", children);

		JsonArray itemRenderOrder = new JsonArray();
		for (String name : this.itemRenderOrder) {
			itemRenderOrder.add(name);
		}
		json.add("item_render_order", itemRenderOrder);

		return json;
	}
}
