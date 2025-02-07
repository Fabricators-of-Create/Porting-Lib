package io.github.fabricators_of_create.porting_lib.render;

import com.mojang.datafixers.util.Pair;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.item.ItemStack;

public interface LayeredBakedModel {
	/**
	 * If {@see isLayered()} returns true, this is called to get the list of layers to draw.
	 */
	default List<Pair<BakedModel, RenderLayer>> getLayerModels(ItemStack itemStack, boolean fabulous) {
		return Collections.singletonList(Pair.of((BakedModel) this, RenderLayers.getItemLayer(itemStack, fabulous)));
	}
}
