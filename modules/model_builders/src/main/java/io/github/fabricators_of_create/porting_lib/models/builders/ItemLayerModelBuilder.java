package io.github.fabricators_of_create.porting_lib.models.builders;

import java.util.Arrays;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import io.github.fabricators_of_create.porting_lib.data.ExistingFileHelper;
import io.github.fabricators_of_create.porting_lib.models.generators.CustomLoaderBuilder;
import io.github.fabricators_of_create.porting_lib.models.materials.MaterialData;
import io.github.fabricators_of_create.porting_lib.models.generators.ModelBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;

public class ItemLayerModelBuilder<T extends ModelBuilder<T>> extends CustomLoaderBuilder<T> {
	public static <T extends ModelBuilder<T>> ItemLayerModelBuilder<T> begin(T parent, ExistingFileHelper existingFileHelper) {
		return new ItemLayerModelBuilder<>(parent, existingFileHelper);
	}

	private final Int2ObjectMap<MaterialData> faceData = new Int2ObjectOpenHashMap<>();

	protected ItemLayerModelBuilder(T parent, ExistingFileHelper existingFileHelper) {
		super(PortingLib.id("item_layers"), parent, existingFileHelper);
	}

	/**
	 * Marks a set of layers to be rendered emissively.
	 *
	 * @param emissive true or false
	 * @param layers the layers that will render unlit
	 * @return this builder
	 * @throws NullPointerException     if {@code layers} is {@code null}
	 * @throws IllegalArgumentException if {@code layers} is empty
	 * @throws IllegalArgumentException if any entry in {@code layers} is smaller than 0
	 */
	public ItemLayerModelBuilder<T> emissive(boolean emissive, int... layers) {
		Preconditions.checkNotNull(layers, "Layers must not be null");
		Preconditions.checkArgument(layers.length > 0, "At least one layer must be specified");
		Preconditions.checkArgument(Arrays.stream(layers).allMatch(i -> i >= 0), "All layers must be >= 0");
		for(int i : layers) {
			faceData.compute(i, (key, value) -> {
				MaterialData fallback = value == null ? MaterialData.DEFAULT : value;
				return new MaterialData(fallback.color(), emissive, fallback.ambientOcclusion(), fallback.blendMode());
			});
		}
		return this;
	}

	/**
	 * Marks a set of layers to be rendered with a specific color.
	 *
	 * @param color The color, in ARGB.
	 * @param layers the layers that will render with color
	 * @return this builder
	 * @throws NullPointerException     if {@code layers} is {@code null}
	 * @throws IllegalArgumentException if {@code layers} is empty
	 * @throws IllegalArgumentException if any entry in {@code layers} is smaller than 0
	 */
	public ItemLayerModelBuilder<T> color(int color, int... layers) {
		Preconditions.checkNotNull(layers, "Layers must not be null");
		Preconditions.checkArgument(layers.length > 0, "At least one layer must be specified");
		Preconditions.checkArgument(Arrays.stream(layers).allMatch(i -> i >= 0), "All layers must be >= 0");
		for(int i : layers) {
			faceData.compute(i, (key, value) -> {
				MaterialData fallback = value == null ? MaterialData.DEFAULT : value;
				return new MaterialData(color, fallback.emissive(), fallback.ambientOcclusion(), fallback.blendMode());
			});
		}
		return this;
	}

	/**
	 * Set the render type for a set of layers.
	 *
	 * @param renderType the render type. Must be registered via
	 *                   {@link net.minecraftforge.client.event.RegisterNamedRenderTypesEvent}
	 * @param layers     the layers that will use this render type
	 * @return this builder
	 * @throws NullPointerException     if {@code renderType} is {@code null}
	 * @throws NullPointerException     if {@code layers} is {@code null}
	 * @throws IllegalArgumentException if {@code layers} is empty
	 * @throws IllegalArgumentException if any entry in {@code layers} is smaller than 0
	 * @throws IllegalArgumentException if any entry in {@code layers} already has a render type
	 */
	public ItemLayerModelBuilder<T> renderType(String renderType, int... layers) {
		Preconditions.checkNotNull(renderType, "Render type must not be null");
		ResourceLocation asLoc;
		if (renderType.contains(":"))
			asLoc = new ResourceLocation(renderType);
		else
			asLoc = new ResourceLocation(parent.getLocation().getNamespace(), renderType);
		return renderType(asLoc, layers);
	}

	/**
	 * Set the render type for a set of layers.
	 *
	 * @param renderType the render type. Must be registered via
	 *                   {@link net.minecraftforge.client.event.RegisterNamedRenderTypesEvent}
	 * @param layers     the layers that will use this render type
	 * @return this builder
	 * @throws NullPointerException     if {@code renderType} is {@code null}
	 * @throws NullPointerException     if {@code layers} is {@code null}
	 * @throws IllegalArgumentException if {@code layers} is empty
	 * @throws IllegalArgumentException if any entry in {@code layers} is smaller than 0
	 * @throws IllegalArgumentException if any entry in {@code layers} already has a render type
	 */
	public ItemLayerModelBuilder<T> renderType(ResourceLocation renderType, int... layers) {
		Preconditions.checkNotNull(renderType, "Render type must not be null");
		Preconditions.checkNotNull(layers, "Layers must not be null");
		Preconditions.checkArgument(layers.length > 0, "At least one layer must be specified");
		Preconditions.checkArgument(Arrays.stream(layers).allMatch(i -> i >= 0), "All layers must be >= 0");
		for(int i : layers) {
			faceData.compute(i, (key, value) -> {
				MaterialData fallback = value == null ? MaterialData.DEFAULT : value;
				return new MaterialData(fallback.color(), fallback.emissive(), fallback.ambientOcclusion(), renderType);
			});
		}
		return this;
	}

	@Override
	public JsonObject toJson(JsonObject json) {
		json = super.toJson(json);

		JsonObject forgeData = new JsonObject();
		JsonObject layerObj = new JsonObject();

		for(Int2ObjectMap.Entry<MaterialData> entry : this.faceData.int2ObjectEntrySet()) {
			layerObj.add(String.valueOf(entry.getIntKey()), MaterialData.CODEC.encodeStart(JsonOps.INSTANCE, entry.getValue()).getOrThrow(false, s -> {}));
		}

		forgeData.add("layers", layerObj);
		json.add("render_materials", forgeData);

		return json;
	}
}
