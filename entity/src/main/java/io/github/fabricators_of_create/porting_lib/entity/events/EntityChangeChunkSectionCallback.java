package io.github.fabricators_of_create.porting_lib.entity.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntitySection;

public interface EntityChangeChunkSectionCallback {
	/**
	 * Fired when an entity crosses the boundary between two chunk sections.
	 * Fired on both client and server.
	 */
	Event<EntityChangeChunkSectionCallback> EVENT = EventFactory.createArrayBacked(EntityChangeChunkSectionCallback.class, callbacks -> (entity, oldSection, oldSectionKey, newSection, newSectionKey) -> {
		for (EntityChangeChunkSectionCallback callback : callbacks)
			callback.onChunkSectionChange(entity, oldSection, oldSectionKey, newSection, newSectionKey);
	});

	/**
	 *
	 */
	void onChunkSectionChange(Entity entity,
							  EntitySection<? extends EntityAccess> oldSection, long oldSectionKey,
							  EntitySection<? extends EntityAccess> newSection, long newSectionKey);
}
