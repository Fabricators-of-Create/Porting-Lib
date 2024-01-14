package io.github.fabricators_of_create.porting_lib.models;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

import com.google.gson.JsonParseException;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

public class ItemLayerModel implements UnbakedModel {
	private final BlockModel owner;
	@Nullable
	private ImmutableList<Material> textures;
	private final Int2ObjectMap<RenderMaterial> layerData;

	private ItemLayerModel(BlockModel owner, @Nullable ImmutableList<Material> textures, Int2ObjectMap<RenderMaterial> layerData) {
		this.owner = owner;
		this.textures = textures;
		this.layerData = layerData;
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return Collections.emptyList();
	}

	@Override
	public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter) {}

	@Nullable
	@Override
	public BakedModel bake(ModelBaker modelBaker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ResourceLocation modelLocation) {
		if (textures == null) {
			ImmutableList.Builder<Material> builder = ImmutableList.builder();
			if (owner.hasTexture("particle"))
				builder.add(owner.getMaterial("particle"));
			for (int i = 0; owner.hasTexture("layer" + i); i++)
			{
				builder.add(owner.getMaterial("layer" + i));
			}
			textures = builder.build();
		}

		TextureAtlasSprite particle = spriteGetter.apply(
				owner.hasTexture("particle") ? owner.getMaterial("particle") : textures.get(0)
		);

		MeshBuilder meshBuilder = RendererAccess.INSTANCE.getRenderer().meshBuilder();
		for (int i = 0; i < textures.size(); i++)
		{
			QuadEmitter emitter = meshBuilder.getEmitter();
			TextureAtlasSprite sprite = spriteGetter.apply(textures.get(i));
			var unbaked = ModelBakery.ITEM_MODEL_GENERATOR.processFrames(i, "layer" + i, sprite.contents());
			var quads = UnbakedGeometryHelper.bakeElements(unbaked, $ -> sprite, modelState, modelLocation);
			for (BakedQuad quad : quads) {
				emitter.fromVanilla(quad, this.layerData.get(i), quad.getDirection());
				emitter.emit();
			}
		}

		return new BakedMeshModel(owner, particle, meshBuilder.build());
	}

	public static final class Loader implements ModelLoader {
		public static final Loader INSTANCE = new Loader();

		@Override
		public UnbakedModel readModel(BlockModel parent, JsonObject jsonObject) {
			if (!RendererAccess.INSTANCE.hasRenderer())
				throw new JsonParseException("The Fabric Rendering API is not available. If you have Sodium, install Indium!");
			var emissiveLayers = new Int2ObjectArrayMap<RenderMaterial>();
			if(jsonObject.has("render_materials")) {
				JsonObject forgeData = jsonObject.get("render_materials").getAsJsonObject();
				readLayerData(forgeData, "layers", emissiveLayers);
			}
			return new ItemLayerModel(parent, null, emissiveLayers);
		}

		public void readLayerData(JsonObject jsonObject, String name, Int2ObjectMap<RenderMaterial> layerData) {
			if (!jsonObject.has(name)) {
				return;
			}
			var fullbrightLayers = jsonObject.getAsJsonObject(name);
			for (var entry : fullbrightLayers.entrySet()) {
				int layer = Integer.parseInt(entry.getKey());
				var data = PortingLibModelLoadingRegistry.GSON.fromJson(entry.getValue(), RenderMaterial.class);
				layerData.put(layer, data);
			}
		}
	}
}
