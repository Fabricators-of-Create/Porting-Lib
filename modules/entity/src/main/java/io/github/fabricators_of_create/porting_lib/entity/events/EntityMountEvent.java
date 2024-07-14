package io.github.fabricators_of_create.porting_lib.entity.events;

import io.github.fabricators_of_create.porting_lib.core.event.CancellableEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

/**
 * This event gets fired whenever a entity mounts/dismounts another entity.<br>
 * <b>entityBeingMounted can be null</b>, be sure to check for that.
 * <br>
 * <br>
 * This event is {@link CancellableEvent}.<br>
 * If this event is canceled, the entity does not mount/dismount the other entity.<br>
 *
 */
public class EntityMountEvent extends EntityEvent implements CancellableEvent {
	public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
		for (final Callback callback : callbacks)
			callback.onEntityMounting(event);
	});

	private final Entity entityMounting;
	private final Entity entityBeingMounted;
	private final Level level;

	private final boolean isMounting;

	public EntityMountEvent(Entity entityMounting, Entity entityBeingMounted, Level level, boolean isMounting) {
		super(entityMounting);
		this.entityMounting = entityMounting;
		this.entityBeingMounted = entityBeingMounted;
		this.level = level;
		this.isMounting = isMounting;
	}

	public boolean isMounting() {
		return isMounting;
	}

	public boolean isDismounting() {
		return !isMounting;
	}

	public Entity getEntityMounting() {
		return entityMounting;
	}

	public Entity getEntityBeingMounted() {
		return entityBeingMounted;
	}

	public Level getLevel() {
		return level;
	}

	@Override
	public void sendEvent() {
		EVENT.invoker().onEntityMounting(this);
	}

	public interface Callback {
		void onEntityMounting(EntityMountEvent event);
	}
}
