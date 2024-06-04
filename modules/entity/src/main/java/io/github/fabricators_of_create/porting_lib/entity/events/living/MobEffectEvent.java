package io.github.fabricators_of_create.porting_lib.entity.events.living;

import io.github.fabricators_of_create.porting_lib.entity.events.LivingEntityEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This event is fired when an interaction between a {@link LivingEntity} and {@link MobEffectInstance} happens.
 */
public abstract class MobEffectEvent extends LivingEntityEvents {
	public static final Event<RemoveCallback> REMOVE = EventFactory.createArrayBacked(RemoveCallback.class, callbacks -> event -> {
		for (RemoveCallback c : callbacks)
			c.onEffectRemove(event);
	});
	public static final Event<ApplicableCallback> APPLICABLE = EventFactory.createArrayBacked(ApplicableCallback.class, callbacks -> event -> {
		for (ApplicableCallback c : callbacks)
			c.onEffectApplicable(event);
	});
	public static final Event<AddedCallback> ADDED = EventFactory.createArrayBacked(AddedCallback.class, callbacks -> event -> {
		for (AddedCallback c : callbacks)
			c.onEffectAdded(event);
	});
	public static final Event<ExpiredCallback> EXPIRED = EventFactory.createArrayBacked(ExpiredCallback.class, callbacks -> event -> {
		for (ExpiredCallback c : callbacks)
			c.onEffectExpired(event);
	});

	@Nullable
	protected final MobEffectInstance effectInstance;

	public MobEffectEvent(LivingEntity living, MobEffectInstance effectInstance) {
		super(living);
		this.effectInstance = effectInstance;
	}

	@Nullable
	public MobEffectInstance getEffectInstance() {
		return effectInstance;
	}

	/**
	 * This Event is fired when a {@link MobEffect} is about to get removed from an Entity.
	 * This Event is cancelable. If canceled, the effect will not be removed.
	 * This Event does not have a result.
	 */
	public static class Remove extends MobEffectEvent {
		private final MobEffect effect;

		public Remove(LivingEntity living, MobEffect effect) {
			super(living, living.getEffect(effect));
			this.effect = effect;
		}

		public Remove(LivingEntity living, MobEffectInstance effectInstance) {
			super(living, effectInstance);
			this.effect = effectInstance.getEffect();
		}

		/**
		 * @return the {@link MobEffectEvent} which is being removed from the entity
		 */
		public MobEffect getEffect() {
			return this.effect;
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
			REMOVE.invoker().onEffectRemove(this);
		}
	}

	/**
	 * This event is fired to check if a {@link MobEffectInstance} can be applied to an entity.
	 * This event is not cancelable.
	 * This event has a result.
	 * <p>
	 * {@link Result#ALLOW ALLOW} will apply this mob effect.
	 * {@link Result#DENY DENY} will not apply this mob effect.
	 * {@link Result#DEFAULT DEFAULT} will run vanilla logic to determine if this mob effect is applicable in {@link LivingEntity#canBeAffected}.
	 */
	public static class Applicable extends MobEffectEvent {
		public Applicable(LivingEntity living, @NotNull MobEffectInstance effectInstance) {
			super(living, effectInstance);
		}

		@Override
		@NotNull
		public MobEffectInstance getEffectInstance() {
			return super.getEffectInstance();
		}

		@Override
		public void sendEvent() {
			APPLICABLE.invoker().onEffectApplicable(this);
		}
	}

	/**
	 * This event is fired when a new {@link MobEffectInstance} is added to an entity.
	 * This event is also fired if an entity already has the effect but with a different duration or amplifier.
	 * This event is not cancelable.
	 * This event does not have a result.
	 */
	public static class Added extends MobEffectEvent {
		private final MobEffectInstance oldEffectInstance;
		private final Entity source;

		public Added(LivingEntity living, MobEffectInstance oldEffectInstance, MobEffectInstance newEffectInstance, Entity source) {
			super(living, newEffectInstance);
			this.oldEffectInstance = oldEffectInstance;
			this.source = source;
		}

		/**
		 * @return the added {@link MobEffectInstance}. This is the unmerged MobEffectInstance if the old MobEffectInstance is not null.
		 */
		@Override
		@NotNull
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
			ADDED.invoker().onEffectAdded(this);
		}
	}

	/**
	 * This event is fired when a {@link MobEffectInstance} expires on an entity.
	 * This event is not cancelable.
	 * This event does not have a result.
	 */
	public static class Expired extends MobEffectEvent {
		public Expired(LivingEntity living, MobEffectInstance effectInstance) {
			super(living, effectInstance);
		}

		@Override
		public void sendEvent() {
			EXPIRED.invoker().onEffectExpired(this);
		}
	}

	public interface RemoveCallback {
		void onEffectRemove(Remove event);
	}

	public interface ApplicableCallback {
		void onEffectApplicable(Applicable event);
	}

	public interface AddedCallback {
		void onEffectAdded(Added event);
	}

	public interface ExpiredCallback {
		void onEffectExpired(Expired event);
	}
}
