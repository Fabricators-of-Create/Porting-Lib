package io.github.fabricators_of_create.porting_lib.models.generators.loaders;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import java.util.LinkedHashMap;
import java.util.Map;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import io.github.fabricators_of_create.porting_lib.data.ExistingFileHelper;
import io.github.fabricators_of_create.porting_lib.models.generators.CustomLoaderBuilder;
import io.github.fabricators_of_create.porting_lib.models.generators.ModelBuilder;
import net.minecraft.world.item.ItemDisplayContext;

public class SeparateTransformsModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T> {
	public static <T extends ModelBuilder<T>> SeparateTransformsModelBuilder<T> begin(T parent, ExistingFileHelper existingFileHelper) {
		return new SeparateTransformsModelBuilder<>(parent, existingFileHelper);
	}

	private T base;
	private final Map<String, T> childModels = new LinkedHashMap<>();

	protected SeparateTransformsModelBuilder(T parent, ExistingFileHelper existingFileHelper) {
		super(PortingLib.id("separate_transforms"), parent, existingFileHelper, false);
	}

	public SeparateTransformsModelBuilder<T> base(T modelBuilder) {
		Preconditions.checkNotNull(modelBuilder, "modelBuilder must not be null");
		base = modelBuilder;
		return this;
	}

	public SeparateTransformsModelBuilder<T> perspective(ItemDisplayContext perspective, T modelBuilder) {
		Preconditions.checkNotNull(perspective, "layer must not be null");
		Preconditions.checkNotNull(modelBuilder, "modelBuilder must not be null");
		childModels.put(perspective.getSerializedName(), modelBuilder);
		return this;
	}

	@Override
	public JsonObject toJson(JsonObject json) {
		json = super.toJson(json);

		if (base != null) {
			json.add("base", base.toJson());
		}

		JsonObject parts = new JsonObject();
		for (Map.Entry<String, T> entry : childModels.entrySet()) {
			parts.add(entry.getKey(), entry.getValue().toJson());
		}
		json.add("perspectives", parts);

		return json;
	}
}
