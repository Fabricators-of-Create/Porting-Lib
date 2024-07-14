package io.github.fabricators_of_create.porting_lib.entity.events.living;

import io.github.fabricators_of_create.porting_lib.core.event.CancellableEvent;
import io.github.fabricators_of_create.porting_lib.entity.EffectCure;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * This event is fired when an interaction between a {@link LivingEntity} and {@link MobEffectInstance} happens.
 */
public abstract class MobEffectEvent extends LivingEvent {
	@Nullable
	protected final MobEffectInstance effectInstance;

	protected MobEffectEvent(LivingEntity living, MobEffectInstance effectInstance) {
		super(living);
		this.effectInstance = effectInstance;
	}

	@Nullable
	public MobEffectInstance getEffectInstance() {
		return effectInstance;
	}

	/**
	 * This Event is fired when a {@link MobEffect} is about to get removed from an Entity.
	 * This Event is {@link CancellableEvent}. If canceled, the effect will not be removed.
	 * This Event does not have a result.
	 */
	public static class Remove extends MobEffectEvent implements CancellableEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (final Callback callback : callbacks)
				callback.onRemoveEffect(event);
		});

		private final Holder<MobEffect> effect;
		@Nullable
		private final EffectCure cure;

		@ApiStatus.Internal
		public Remove(LivingEntity living, Holder<MobEffect> effect, @Nullable EffectCure cure) {
			super(living, living.getEffect(effect));
			this.effect = effect;
			this.cure = cure;
		}

		@ApiStatus.Internal
		public Remove(LivingEntity living, MobEffectInstance effectInstance, @Nullable EffectCure cure) {
			super(living, effectInstance);
			this.effect = effectInstance.getEffect();
			this.cure = cure;
		}

		/**
		 * @return the {@link MobEffect} which is being removed from the entity
		 */
		public Holder<MobEffect> getEffect() {
			return this.effect;
		}

		/**
		 * {@return the {@link EffectCure} the effect is being cured by. Null if the effect is not removed due to being cured}
		 */
		@Nullable
		public EffectCure getCure() {
			return cure;
		}

		/**
		 * @return the {@link MobEffectInstance}. In the remove event, this can be null if the entity does not have a {@link MobEffect} of the right type active.
		 */
		@Override
		@Nullable
		public MobEffectInstance getEffectInstance() {
			return super.getEffectInstance();
		}

		@Override
		public void sendEvent() {
			EVENT.invoker().onRemoveEffect(this);
		}

		public interface Callback {
			void onRemoveEffect(Remove event);
		}
	}

	/**
	 * This event is fired to check if a {@link MobEffectInstance} can be applied to an entity.
	 * <p>
	 * It will be fired whenever {@link LivingEntity#canBeAffected(MobEffectInstance)} would be invoked.
	 * <p>
	 *
	 */
	public static class Applicable extends MobEffectEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> effect -> {
			for (final Callback callback : callbacks)
				callback.onEffectApplicable(effect);
		});

		protected Result result = Result.DEFAULT;

		@ApiStatus.Internal
		public Applicable(LivingEntity living, MobEffectInstance effectInstance) {
			super(living, effectInstance);
		}

		@Override
		public MobEffectInstance getEffectInstance() {
			return super.getEffectInstance();
		}

		/**
		 * Changes the result of this event.
		 *
		 * @see {@link Result} for the possible states.
		 */
		public void setResult(Result result) {
			this.result = result;
		}

		/**
		 * {@return the result of this event, which controls if the mob effect will be applied}
		 */
		public Result getResult() {
			return this.result;
		}

		/**
		 * {@return If the mob effect should be applied or not, based on the current event result}
		 */
		@SuppressWarnings("deprecation") // Expected as the single call site for canBeAffected
		public boolean getApplicationResult() {
			if (this.result == Result.APPLY) {
				return true;
			}
			return this.result == Result.DEFAULT && this.getEntity().canBeAffected(this.getEffectInstance());
		}

		@Override
		public void sendEvent() {
			EVENT.invoker().onEffectApplicable(this);
		}

		public static enum Result {
			/**
			 * Forces the event to apply the mob effect to the target entity.
			 */
			APPLY,

			/**
			 * The result of {@link LivingEntity#canBeAffected(MobEffectInstance)} will be used to determine if the mob effect will be applied.
			 *
			 * @see {@link Post#wasClickHandled()}
			 */
			DEFAULT,

			/**
			 * Forces the event to not apply the mob effect.
			 */
			DO_NOT_APPLY;
		}

		public interface Callback {
			void onEffectApplicable(Applicable event);
		}
	}

	/**
	 * This event is fired when a new {@link MobEffectInstance} is added to an entity.
	 * This event is also fired if an entity already has the effect but with a different duration or amplifier.
	 * This event is not {@link CancellableEvent}.
	 * This event does not have a result.
	 */
	public static class Added extends MobEffectEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (final Callback callback : callbacks)
				callback.onEffectAdded(event);
		});

		private final MobEffectInstance oldEffectInstance;
		private final Entity source;

		@ApiStatus.Internal
		public Added(LivingEntity living, MobEffectInstance oldEffectInstance, MobEffectInstance newEffectInstance, Entity source) {
			super(living, newEffectInstance);
			this.oldEffectInstance = oldEffectInstance;
			this.source = source;
		}

		/**
		 * @return the added {@link MobEffectInstance}. This is the unmerged MobEffectInstance if the old MobEffectInstance is not null.
		 */
		@Override
		public MobEffectInstance getEffectInstance() {
			return super.getEffectInstance();
		}

		/**
		 * @return the old {@link MobEffectInstance}. This can be null if the entity did not have an effect of this kind before.
		 */
		@Nullable
		public MobEffectInstance getOldEffectInstance() {
			return oldEffectInstance;
		}

		/**
		 * @return the entity source of the effect, or {@code null} if none exists
		 */
		@Nullable
		public Entity getEffectSource() {
			return source;
		}

		@Override
		public void sendEvent() {
			EVENT.invoker().onEffectAdded(this);
		}

		public interface Callback {
			void onEffectAdded(Added event);
		}
	}

	/**
	 * This event is fired when a {@link MobEffectInstance} expires on an entity.
	 * This event is {@link CancellableEvent}.
	 * This event does not have a result.
	 */
	public static class Expired extends MobEffectEvent implements CancellableEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (final Callback callback : callbacks)
				callback.onEffectExpired(event);
		});

		@ApiStatus.Internal
		public Expired(LivingEntity living, MobEffectInstance effectInstance) {
			super(living, effectInstance);
		}

		@Override
		public void sendEvent() {
			EVENT.invoker().onEffectExpired(this);
		}

		public interface Callback {
			void onEffectExpired(Expired event);
		}
	}
}
