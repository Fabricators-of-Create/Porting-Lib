package io.github.fabricators_of_create.porting_lib.event.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

public interface ModelLoadCallback {
	Event<ModelLoadCallback> EVENT = EventFactory.createArrayBacked(ModelLoadCallback.class, callbacks -> (manager, colors, profiler, mipLevel) -> {
		for (ModelLoadCallback e : callbacks)
			e.onModelsStartLoading(manager, colors, profiler, mipLevel);
	});


	void onModelsStartLoading(ResourceManager manager, BlockColors colors, ProfilerFiller profiler, int mipLevel);
}
