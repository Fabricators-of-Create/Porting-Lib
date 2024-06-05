package io.github.fabricators_of_create.porting_lib.event.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.List;
import java.util.Map;

public interface ModelLoadCallback {
	Event<ModelLoadCallback> EVENT = EventFactory.createArrayBacked(ModelLoadCallback.class, callbacks -> (colors, profiler, modelResources, blockStateResources) -> {
		for (ModelLoadCallback e : callbacks)
			e.onModelsStartLoading(colors, profiler, modelResources, blockStateResources);
	});


	void onModelsStartLoading(BlockColors colors, ProfilerFiller profiler, Map<ResourceLocation, BlockModel> modelResources, Map<ResourceLocation, List<BlockStateModelLoader.LoadedJson>> blockStateResources);
}
