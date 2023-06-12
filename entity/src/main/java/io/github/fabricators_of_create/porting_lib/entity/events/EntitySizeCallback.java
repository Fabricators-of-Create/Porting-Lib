package io.github.fabricators_of_create.porting_lib.entity.events;

import io.github.fabricators_of_create.porting_lib.core.event.CancellableEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;

/**
 * Allows for modifying the eye height and dimensions of an entity.
 */
public interface EntitySizeCallback {
	Event<EntitySizeCallback> EVENT = EventFactory.createArrayBacked(EntitySizeCallback.class, callbacks -> (event) -> {
		for (EntitySizeCallback callback : callbacks) {
			callback.modifySize(event);
			if (event.isCancelled())
				return;
		}
	});

	void modifySize(EntitySizeEvent event);

	class EntitySizeEvent extends CancellableEvent {
		public final Entity entity;
		public final Pose pose;
		public final float originalEyeHeight;
		public final EntityDimensions originalDimensions;

		public float eyeHeight;
		public EntityDimensions dimensions;

		public EntitySizeEvent(Entity entity, Pose pose, float height, EntityDimensions dimensions) {
			this(entity, pose, height, height, dimensions, dimensions);
		}

		public EntitySizeEvent(Entity entity, Pose pose, float oldHeight, float newHeight, EntityDimensions oldDimensions, EntityDimensions newDimensions) {
			this.entity = entity;
			this.pose = pose;
			this.originalEyeHeight = oldHeight;
			this.eyeHeight = newHeight;
			this.originalDimensions = oldDimensions;
			this.dimensions = newDimensions;
		}
	}
}
