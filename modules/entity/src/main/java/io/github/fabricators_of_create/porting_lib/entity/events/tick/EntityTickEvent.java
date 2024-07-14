package io.github.fabricators_of_create.porting_lib.entity.events.tick;

import io.github.fabricators_of_create.porting_lib.core.event.CancellableEvent;
import io.github.fabricators_of_create.porting_lib.entity.events.EntityEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.Entity;

/**
 * Base class of the two entity tick events.
 *
 * @see EntityTickEvent.Pre
 * @see EntityTickEvent.Post
 */
public abstract class EntityTickEvent extends EntityEvent {
	protected EntityTickEvent(Entity entity) {
		super(entity);
	}

	/**
	 * {@link EntityTickEvent.Pre} is fired once per game tick, per entity, before the entity performs work for the current tick.
	 * <p>
	 * This event fires on both the logical server and logical client.
	 */
	public static class Pre extends EntityTickEvent implements CancellableEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (final Callback callback : callbacks)
				callback.onPreTick(event);
		});

		public Pre(Entity entity) {
			super(entity);
		}

		/**
		 * Cancels this event, preventing the current tick from being executed for the entity.
		 * <p>
		 * Additionally, if this event is canceled, then {@link EntityTickEvent.Post} will not be fired for the current tick.
		 */
		@Override
		public void setCanceled(boolean canceled) {
			CancellableEvent.super.setCanceled(canceled);
		}

		@Override
		public void sendEvent() {
			EVENT.invoker().onPreTick(this);
		}

		public interface Callback {
			void onPreTick(Pre event);
		}
	}

	/**
	 * {@link EntityTickEvent.Post} is fired once per game tick, per entity, after the entity performs work for the current tick.
	 * <p>
	 * If {@link EntityTickEvent.Pre} was canceled for the current tick, this event will not fire.
	 * <p>
	 * This event fires on both the logical server and logical client.
	 */
	public static class Post extends EntityTickEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (final Callback callback : callbacks)
				callback.onPostTick(event);
		});

		public Post(Entity entity) {
			super(entity);
		}

		@Override
		public void sendEvent() {
			EVENT.invoker().onPostTick(this);
		}

		public interface Callback {
			void onPostTick(Post event);
		}
	}
}
