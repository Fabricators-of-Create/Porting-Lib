package io.github.fabricators_of_create.porting_lib.core.event.entity.living;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;
import io.github.fabricators_of_create.porting_lib.core.event.entity.EntityEvent;
import net.minecraft.world.entity.LivingEntity;

/**
 * LivingEvent is fired whenever an event involving a {@link LivingEntity} occurs.<br>
 * If a method utilizes this {@link BaseEvent} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 **/
public abstract class LivingEvent extends EntityEvent {
	private final LivingEntity livingEntity;

	public LivingEvent(LivingEntity entity) {
		super(entity);
		livingEntity = entity;
	}

	@Override
	public LivingEntity getEntity() {
		return livingEntity;
	}
}
