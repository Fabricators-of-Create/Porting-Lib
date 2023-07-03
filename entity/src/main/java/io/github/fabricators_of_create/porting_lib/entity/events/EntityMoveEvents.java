package io.github.fabricators_of_create.porting_lib.entity.events;

import io.github.fabricators_of_create.porting_lib.core.event.CancelBypass;
import io.github.fabricators_of_create.porting_lib.core.event.object.CancellableEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntitySection;

public class EntityMoveEvents {
	/**
	 * Fired when an entity crosses the boundary between two chunk sections.
	 * Fired on both client and server.
	 */
	public static final Event<ChunkSectionChange> CHUNK_SECTION_CHANGE = EventFactory.createArrayBacked(ChunkSectionChange.class, callbacks -> ctx -> {
		for (ChunkSectionChange callback : callbacks)
			callback.onChunkSectionChange(ctx);
	});

	@FunctionalInterface
	public interface ChunkSectionChange {
		void onChunkSectionChange(ChunkSectionChangeContext ctx);
	}

	public record ChunkSectionChangeContext(
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

	/**
	 * Called when an entity is teleported. Handled scenarios:
	 * <ul>
	 *     <li>eating Chorus Fruit</li>
	 *     <li>/spreadplayers</li>
	 *     <li>/tp or /teleport</li>
	 *     <li>Enderman teleport</li>
	 *     <li>Ender Pearls</li>
	 * </ul>
	 */
	public static final Event<Teleport> TELEPORT = CancelBypass.makeEvent(Teleport.class, holder -> callbacks -> event -> {
		for (Teleport callback : callbacks) {
			if (event.shouldInvokeListener(holder, callback))
				callback.onTeleport(event);
		}
	});

	@FunctionalInterface
	public interface Teleport {
		/**
		 * Called when an entity teleports. The target position of the teleport may be modified,
		 * and the teleport may be cancelled as a whole.
		 */
		void onTeleport(EntityTeleportEvent event);
	}

	public static class EntityTeleportEvent extends CancellableEvent {
		public final Entity entity;
		public double targetX, targetY, targetZ;

		public EntityTeleportEvent(Entity entity, double targetX, double targetY, double targetZ) {
			this.entity = entity;
			this.targetX = targetX;
			this.targetY = targetY;
			this.targetZ = targetZ;
		}
	}
}
