package io.github.fabricators_of_create.porting_lib.entity.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntitySection;

public interface EntityChangeChunkSectionCallback {
	/**
	 * Fired when an entity crosses the boundary between two chunk sections.
	 * Fired on both client and server.
	 */
	Event<EntityChangeChunkSectionCallback> EVENT = EventFactory.createArrayBacked(EntityChangeChunkSectionCallback.class, callbacks -> ctx -> {
		for (EntityChangeChunkSectionCallback callback : callbacks)
			callback.onChunkSectionChange(ctx);
	});

	void onChunkSectionChange(Context ctx);

	record Context(
			Entity entity,
			EntitySection<? extends EntityAccess> oldSection, long oldPackedPos,
			EntitySection<? extends EntityAccess> newSection, long newPackedPos
	) {
		public SectionPos oldPos() {
			return SectionPos.of(oldPackedPos);
		}

		public SectionPos newPos() {
			return SectionPos.of(newPackedPos);
		}
	}
}
