package io.github.fabricators_of_create.porting_lib.event.client;

import io.github.fabricators_of_create.porting_lib.model.geometry.IGeometryLoader;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public interface RegisterGeometryLoadersCallback {
	Event<RegisterGeometryLoadersCallback> EVENT = EventFactory.createArrayBacked(RegisterGeometryLoadersCallback.class, callbacks -> loaders -> {
		for (RegisterGeometryLoadersCallback e : callbacks)
			e.registerGeometryLoaders(loaders);
	});

	void registerGeometryLoaders(Map<ResourceLocation, IGeometryLoader<?>> loaders);
}
