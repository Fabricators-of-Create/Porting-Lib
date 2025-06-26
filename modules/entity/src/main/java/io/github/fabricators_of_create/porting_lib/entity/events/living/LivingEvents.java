package io.github.fabricators_of_create.porting_lib.entity.events.living;

import javax.annotation.Nullable;

import io.github.fabricators_of_create.porting_lib.core.event.CancellableEvent;
import io.github.fabricators_of_create.porting_lib.core.event.entity.living.LivingEvent;
import io.github.fabricators_of_create.porting_lib.entity.EntityHooks;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/**
 * All Living entity related events
 */
public final class LivingEvents {
	private LivingEvents() {}

	/**
	 * LivingJumpEvent is fired when an Entity jumps.<br>
	 * This event is fired whenever an Entity jumps in
	 * {@code LivingEntity#jumpFromGround()}, {@code MagmaCube#jumpFromGround()},
	 * {@code Slime#jumpFromGround()}, {@code Camel#executeRidersJump()},
	 * and {@code AbstractHorse#executeRidersJump()}.<br>
	 * <br>
	 * This event is fired via the {@link EntityHooks#onLivingJump(LivingEntity)}.<br>
	 * <br>
	 * This event is not {@link CancellableEvent}.<br>
	 **/
	public static class LivingJumpEvent extends LivingEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> (event) -> {
			for (Callback callback : callbacks)
				callback.onLivingEntityJump(event);
		});

		public LivingJumpEvent(LivingEntity e) {
			super(e);
		}

		@Override
		public void sendEvent() {
			EVENT.invoker().onLivingEntityJump(this);
		}

		public interface Callback {
			void onLivingEntityJump(LivingJumpEvent event);
		}
	}

	public static class LivingVisibilityEvent extends LivingEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (Callback e : callbacks)
				e.onLivingVisibility(event);
		});
		private double visibilityModifier;
		@Nullable
		private final Entity lookingEntity;

		public LivingVisibilityEvent(LivingEntity livingEntity, @Nullable Entity lookingEntity, double originalMultiplier) {
			super(livingEntity);
			this.visibilityModifier = originalMultiplier;
			this.lookingEntity = lookingEntity;
		}

		/**
		 * @param mod Is multiplied with the current modifier
		 */
		public void modifyVisibility(double mod) {
			visibilityModifier *= mod;
		}

		/**
		 * @return The current modifier
		 */
		public double getVisibilityModifier() {
			return visibilityModifier;
		}

		/**
		 * @return The entity trying to see this LivingEntity, if available
		 */
		@Nullable
		public Entity getLookingEntity() {
			return lookingEntity;
		}

		@Override
		public void sendEvent() {
			EVENT.invoker().onLivingVisibility(this);
		}

		public interface Callback {
			void onLivingVisibility(LivingVisibilityEvent event);
		}
	}
}
