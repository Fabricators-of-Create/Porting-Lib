package io.github.fabricators_of_create.porting_lib.core.event.entity;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;
import net.minecraft.world.entity.Entity;

/**
 * EntityEvent is fired when an event involving any Entity occurs.<br>
 * If a method utilizes this {@link BaseEvent} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * {@link #entity} contains the entity that caused this event to occur.<br>
 * <br>
 **/
public abstract class EntityEvent extends BaseEvent {
	private final Entity entity;

	public EntityEvent(Entity entity) {
		this.entity = entity;
	}

	public Entity getEntity() {
		return entity;
	}
}
