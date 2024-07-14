package io.github.fabricators_of_create.porting_lib.entity.events.living;

import io.github.fabricators_of_create.porting_lib.core.event.CancellableEvent;
import io.github.fabricators_of_create.porting_lib.entity.EntityHooks;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

/**
 * LivingFallEvent is fired when an Entity is set to be falling.<br>
 * This event is fired whenever an Entity is set to fall in
 * {@link LivingEntity#causeFallDamage(float, float, DamageSource)}.<br>
 * <br>
 * This event is fired via the {@link EntityHooks#onLivingFall(LivingEntity, float, float)}.<br>
 * <br>
 * {@link #distance} contains the distance the Entity is to fall. If this event is canceled, this value is set to 0.0F.
 * <br>
 * This event is {@link CancellableEvent}.<br>
 * If this event is canceled, the Entity does not fall.<br>
 * <br>
 **/
public class LivingFallEvent extends LivingEvent implements CancellableEvent {
	public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
		for (final Callback callback : callbacks)
			callback.onLivingFall(event);
	});

	private float distance;
	private float damageMultiplier;

	public LivingFallEvent(LivingEntity entity, float distance, float damageMultiplier) {
		super(entity);
		this.setDistance(distance);
		this.setDamageMultiplier(damageMultiplier);
	}

	public float getDistance() {
		return distance;
	}

	public void setDistance(float distance) {
		this.distance = distance;
	}

	public float getDamageMultiplier() {
		return damageMultiplier;
	}

	public void setDamageMultiplier(float damageMultiplier) {
		this.damageMultiplier = damageMultiplier;
	}

	@Override
	public void sendEvent() {
		EVENT.invoker().onLivingFall(this);
	}

	public interface Callback {
		void onLivingFall(LivingFallEvent event);
	}
}
