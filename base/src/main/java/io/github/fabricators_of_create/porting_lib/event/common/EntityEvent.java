package io.github.fabricators_of_create.porting_lib.event.common;

import io.github.fabricators_of_create.porting_lib.event.BaseEvent;
import net.minecraft.world.entity.Entity;

public abstract class EntityEvent extends BaseEvent {
	protected final Entity entity;

	public EntityEvent(Entity entity) {
		this.entity = entity;
	}

	public Entity getEntity() {
		return entity;
	}
}
