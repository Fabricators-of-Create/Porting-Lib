package io.github.fabricators_of_create.porting_lib.models;

import com.google.gson.JsonObject;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.UnbakedModel;

public interface ModelLoader {
	UnbakedModel readModel(BlockModel parent, JsonObject jsonObject);
}
