package io.github.fabricators_of_create.porting_lib.entity.events;

import io.github.fabricators_of_create.porting_lib.core.event.CancellableEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.Entity;

public interface EntityTeleportCallback {
	/**
	 * Called when an entity is teleported. Handled scenarios:
	 * <ul>
	 *     <li>eating Chorus Fruit</li>
	 *     <li>/spreadplayers</li>
	 *     <li>/tp and /teleport</li>
	 *     <li>Enderman teleport</li>
	 *     <li>Ender Pearls</li>
	 * </ul>
	 */
	Event<EntityTeleportCallback> EVENT = EventFactory.createArrayBacked(EntityTeleportCallback.class, callbacks -> event -> {
		for (EntityTeleportCallback callback : callbacks) {
			callback.onTeleport(event);
			if (event.isCancelled())
				return;
		}
	});

	/**
	 * Called when an entity teleports. The target position of the teleport may be modified,
	 * and the teleport may be cancelled as a whole.
	 */
	void onTeleport(EntityTeleportEvent event);

	class EntityTeleportEvent extends CancellableEvent.Base {
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
