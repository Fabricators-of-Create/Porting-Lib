package io.github.fabricators_of_create.porting_lib.entity.events;

import io.github.fabricators_of_create.porting_lib.core.event.CancellableEvent;
import io.github.fabricators_of_create.porting_lib.core.event.entity.EntityEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.LevelCallback;

/**
 * This event is fired whenever an {@link Entity} leaves a {@link Level}.
 * This event is fired whenever an entity is removed from the level in {@link LevelCallback#onTrackingEnd(Object)}.
 * <p>
 * This event is not {@linkplain CancellableEvent cancellable} and does not have a result.
 * <p>
 * This event is fired on both logical sides.
 **/
public class EntityLeaveLevelEvent extends EntityEvent {
	public static final Event<EntityLeaveLevelCallback> EVENT = EventFactory.createArrayBacked(EntityLeaveLevelCallback.class, callbacks -> event -> {
		for (EntityLeaveLevelCallback callback : callbacks) {
			callback.onEntityLeaveLevel(event);
		}
	});

	private final Level level;

	public EntityLeaveLevelEvent(Entity entity, Level level) {
		super(entity);
		this.level = level;
	}

	/**
	 * {@return the level the entity is set to leave}
	 */
	public Level getLevel() {
		return level;
	}

	@Override
	public void sendEvent() {
		EVENT.invoker().onEntityLeaveLevel(this);
	}

	@FunctionalInterface
	public interface EntityLeaveLevelCallback {
		void onEntityLeaveLevel(EntityLeaveLevelEvent event);
	}
}

