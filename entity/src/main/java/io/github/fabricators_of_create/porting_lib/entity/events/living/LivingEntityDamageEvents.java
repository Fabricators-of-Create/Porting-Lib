package io.github.fabricators_of_create.porting_lib.entity.events.living;

import io.github.fabricators_of_create.porting_lib.core.event.CancellableEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

public class LivingEntityDamageEvents {

	/**
	 * Fired when an entity takes damage.
	 */
	public static final Event<Hurt> HURT = EventFactory.createArrayBacked(Hurt.class, callbacks -> event -> {
		for (Hurt callback : callbacks) {
			callback.onHurt(event);
			if (event.isCancelled())
				return;
		}
	});

	@FunctionalInterface
	public interface Hurt {
		/**
		 * Called when an entity is hurt. Listeners may change the damage amount, or cancel the damage entirely.
		 */
		void onHurt(HurtEvent event);
	}

	public static class HurtEvent extends CancellableEvent.Base {
		public final LivingEntity damaged;
		public final DamageSource damageSource;

		public float damageAmount;

		public HurtEvent(LivingEntity damaged, DamageSource damageSource, float damageAmount) {
			this.damaged = damaged;
			this.damageSource = damageSource;
			this.damageAmount = damageAmount;
		}
	}

	/**
	 * Fired when an entity takes fall damage.
	 */
	public static final Event<Fall> FALL = EventFactory.createArrayBacked(Fall.class, callbacks -> (event) -> {
		for (Fall callback : callbacks) {
			callback.onFall(event);
			if (event.isCancelled())
				return;
		}
	});

	@FunctionalInterface
	public interface Fall {
		/**
		 * Called when an entity takes fall damage. Listeners may modify the fall
		 * distance and the damage modifier, or cancel the event entirely.
		 */
		void onFall(FallEvent event);
	}

	public static class FallEvent extends CancellableEvent.Base {
		public final LivingEntity entity;
		public final DamageSource damageSource;

		public float distance, damageMultiplier;

		public FallEvent(LivingEntity entity, DamageSource damageSource, float distance, float damageMultiplier) {
			this.entity = entity;
			this.damageSource = damageSource;
			this.distance = distance;
			this.damageMultiplier = damageMultiplier;
		}
	}

	/**
	 * Fired when an entity takes knockback.
	 */
	public static final Event<KnockBackStrength> KNOCKBACK_STRENGTH = EventFactory.createArrayBacked(KnockBackStrength.class, callbacks -> (entity, strength) -> {
		for (KnockBackStrength callback : callbacks) {
			strength = callback.onKnockback(entity, strength);
			if (strength == 0)
				return 0; // cancel
		}
		return strength;
	});


	@FunctionalInterface
	public interface KnockBackStrength {
		/**
		 * @return the new knockback strength, the original to do nothing, or 0 to cancel
		 */
		double onKnockback(LivingEntity entity, double strength);
	}
}
