package io.github.fabricators_of_create.porting_lib.entity.events.living;

import io.github.fabricators_of_create.porting_lib.entity.events.LivingEntityEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

/**
 * LivingHurtEvent is fired when an Entity is set to be hurt. <br>
 * This event is fired whenever an Entity is hurt in
 * {@code LivingEntity#actuallyHurt(DamageSource, float)} and
 * {@code Player#actuallyHurt(DamageSource, float)}.<br>
 * <br>
 * {@link #source} contains the DamageSource that caused this Entity to be hurt. <br>
 * {@link #amount} contains the amount of damage dealt to the Entity that was hurt. <br>
 * <br>
 * This event is cancelable.<br>
 * If this event is canceled, the Entity is not hurt.<br>
 * <br>
 * This event does not have a result.<br>
 * <br>
 *
 * @see LivingDamageEvent
 **/
public class LivingHurtEvent extends LivingEntityEvents {
	public static final Event<HurtCallback> HURT = EventFactory.createArrayBacked(HurtCallback.class, callbacks -> event -> {
		for (HurtCallback e : callbacks)
			e.onLivingHurt(event);
	});
	private final DamageSource source;
	private float amount;

	public LivingHurtEvent(LivingEntity entity, DamageSource source, float amount) {
		super(entity);
		this.source = source;
		this.amount = amount;
	}

	public DamageSource getSource() {
		return source;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	@Override
	public void sendEvent() {
		HURT.invoker().onLivingHurt(this);
	}

	@FunctionalInterface
	public interface HurtCallback {
		void onLivingHurt(LivingHurtEvent event);
	}
}
