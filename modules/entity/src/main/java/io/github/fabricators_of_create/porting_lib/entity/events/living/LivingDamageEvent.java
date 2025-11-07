package io.github.fabricators_of_create.porting_lib.entity.events.living;

import io.github.fabricators_of_create.porting_lib.core.event.entity.living.LivingEvent;
import io.github.fabricators_of_create.porting_lib.entity.EntityHooks;
import io.github.fabricators_of_create.porting_lib.entity.damage.DamageContainer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.stream.Collectors;

/**
 * LivingDamageEvent captures an entity's loss of health. At this stage in
 * the damage sequence, all reduction effects have been applied.
 * <br>
 * {@link Pre} allows for modification of the damage value before it is applied
 * to the entity's health.
 * <br>
 * {@link Post} contains an immutable representation of the entire damage sequence
 * and allows for reference to the values accrued at each step.
 *
 * @see DamageContainer for more information on the damage sequence
 */
public abstract class LivingDamageEvent extends LivingEvent {
	private LivingDamageEvent(LivingEntity entity) {
		super(entity);
	}

	/**
	 * LivingDamageEvent.Pre is fired when an Entity is set to be hurt. <br>
	 * At this point armor, and potion modifiers have already been applied to the damage value
	 * and the entity. Absorption modifiers are handled after this event.<br>
	 * This event is fired in {@code LivingEntity#actuallyHurt(DamageSource, float}
	 * <br>
	 * For custom posting of this event, the event expects to be fired after
	 * damage reductions have been calculated but before any changes to the entity
	 * health has been applied. This event expects a mutable {@link DamageContainer}.
	 * <br>
	 * This event is fired via the {@link EntityHooks#onLivingDamagePre(LivingEntity, DamageContainer)}.
	 *
	 * @see DamageContainer for more information on the damage sequence
	 **/
	public static class Pre extends LivingDamageEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (Callback callback : callbacks) {
				callback.onLivingDamagePre(event);
			}
		});

		public interface Callback {
			void onLivingDamagePre(Pre event);
		}

		@Override
		public void sendEvent() {
			EVENT.invoker().onLivingDamagePre(this);
		}

		private final DamageContainer container;

		public Pre(LivingEntity entity, DamageContainer container) {
			super(entity);
			this.container = container;
		}

		/** {@return the {@link DamageContainer} instance for this damage sequence} */
		public DamageContainer getContainer() {
			return container;
		}

		/** {@return the damage source for this damage sequence} */
		public DamageSource getSource() {
			return container.getSource();
		}

		/** {@return the current value to be applied to the entity's health after this event} */
		public float getNewDamage() {
			return container.getNewDamage();
		}

		/** {@return the original damage amount from the damage source} */
		public float getOriginalDamage() {
			return container.getOriginalDamage();
		}

		/**
		 * Sets the amount to reduce the entity health by
		 *
		 * @param newDamage the new damage value
		 */
		public void setNewDamage(float newDamage) {
			container.setNewDamage(newDamage);
		}
	}

	/**
	 * LivingDamageEvent.Post is fired after health is modified on the entity.<br>
	 * The fields in this event represent the FINAL values of what was applied to the entity.
	 * <br>
	 * Also note that appropriate resources (like armor durability and absorption extra hearts) have already been consumed.<br>
	 * This event is fired whenever an Entity is damaged in {@code LivingEntity#actuallyHurt(DamageSource, float)}
	 * <br>
	 * This event is fired via {@link EntityHooks#onLivingDamagePost(LivingEntity, DamageContainer)}.
	 *
	 * @see DamageContainer for more information on the damage sequence
	 **/
	public static class Post extends LivingDamageEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (Callback callback : callbacks) {
				callback.onLivingDamagePost(event);
			}
		});

		public interface Callback {
			void onLivingDamagePost(Post event);
		}

		@Override
		public void sendEvent() {
			EVENT.invoker().onLivingDamagePost(this);
		}

		private final float originalDamage;
		private final DamageSource source;
		private final float newDamage;
		private final float blockedDamage;
		private final float shieldDamage;
		private final int postAttackInvulnerabilityTicks;
		private final EnumMap<DamageContainer.Reduction, Float> reductions;

		public Post(LivingEntity entity, DamageContainer container) {
			super(entity);
			this.originalDamage = container.getOriginalDamage();
			this.source = container.getSource();
			this.newDamage = container.getNewDamage();
			this.blockedDamage = container.getBlockedDamage();
			this.shieldDamage = container.getShieldDamage();
			this.postAttackInvulnerabilityTicks = container.getPostAttackInvulnerabilityTicks();
			this.reductions = new EnumMap<DamageContainer.Reduction, Float>(Arrays.stream(DamageContainer.Reduction.values())
					.map(type -> new AbstractMap.SimpleEntry<>(type, container.getReduction(type)))
					.collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue)));
		}

		/** {@return the original damage when {@link LivingEntity#hurt} was invoked} */
		public float getOriginalDamage() {
			return originalDamage;
		}

		/** {@return the {@link DamageSource} for this damage sequence} */
		public DamageSource getSource() {
			return source;
		}

		/** {@return the amount of health this entity lost during this sequence} */
		public float getNewDamage() {
			return newDamage;
		}

		/** {@return the amount of damage reduced by a blocking action} */
		public float getBlockedDamage() {
			return blockedDamage;
		}

		/** {@return the amount of shield durability this entity lost if a blocking action was captured and the entity was holding a shield} */
		public float getShieldDamage() {
			return shieldDamage;
		}

		/** {@return the number of ticks this entity will be invulnerable after this sequence} */
		public int getPostAttackInvulnerabilityTicks() {
			return postAttackInvulnerabilityTicks;
		}

		/**
		 * @param reduction the type of reduction to obtain
		 * @return the amount of damage reduced by this reduction type.
		 */
		public float getReduction(DamageContainer.Reduction reduction) {
			return reductions.getOrDefault(reduction, 0f);
		}
	}
}
