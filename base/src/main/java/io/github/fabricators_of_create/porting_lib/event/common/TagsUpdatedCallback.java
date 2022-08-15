package io.github.fabricators_of_create.porting_lib.event.common;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.RegistryAccess;

public interface TagsUpdatedCallback {
	Event<TagsUpdatedCallback> EVENT = EventFactory.createArrayBacked(TagsUpdatedCallback.class, callbacks -> registryAccess -> {
		for (TagsUpdatedCallback e : callbacks)
			e.onTagsUpdated(registryAccess);
	});

	void onTagsUpdated(RegistryAccess registries);
}
