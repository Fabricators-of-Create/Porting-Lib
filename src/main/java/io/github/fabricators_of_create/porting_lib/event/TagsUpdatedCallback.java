package io.github.fabricators_of_create.porting_lib.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.tags.TagContainer;

public interface TagsUpdatedCallback {
	Event<TagsUpdatedCallback> EVENT = EventFactory.createArrayBacked(TagsUpdatedCallback.class, callbacks -> tagContainer -> {
		for (TagsUpdatedCallback callback : callbacks)
			callback.onTagsUpdated(tagContainer);
	});

	void onTagsUpdated(TagContainer tagContainer);
}
