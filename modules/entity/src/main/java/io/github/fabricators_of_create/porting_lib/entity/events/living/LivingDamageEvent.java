package io.github.fabricators_of_create.porting_lib.entity.events.living;

import io.github.fabricators_of_create.porting_lib.entity.events.LivingEntityEvents;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

/**
 * LivingDamageEvent is fired just before damage is applied to entity.<br>
 * At this point armor, potion and absorption modifiers have already been applied to damage - this is FINAL value.<br>
 * Also note that appropriate resources (like armor durability and absorption extra hearths) have already been consumed.<br>
 * This event is fired whenever an Entity is damaged in
 * {@code LivingEntity#actuallyHurt(DamageSource, float)} and
 * {@code Player#actuallyHurt(DamageSource, float)}.<br>
 * <br>
 * {@link #source} contains the DamageSource that caused this Entity to be hurt. <br>
 * {@link #amount} contains the final amount of damage that will be dealt to entity. <br>
 * <br>
 * This event is cancelable.<br>
 * If this event is canceled, the Entity is not hurt. Used resources WILL NOT be restored.<br>
 * <br>
 * This event does not have a result.<br>
 *
 * @see LivingHurtEvent
 **/
public class LivingDamageEvent extends LivingEntityEvents {
	public static final Event<DamageCallback> DAMAGE = EventFactory.createArrayBacked(DamageCallback.class, callbacks -> event -> {
		for (DamageCallback e : callbacks)
			e.onLivingDamage(event);
	});
	private final DamageSource source;
	private float amount;

	public LivingDamageEvent(LivingEntity entity, DamageSource source, float amount) {
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
		DAMAGE.invoker().onLivingDamage(this);
	}

	@FunctionalInterface
	public interface DamageCallback {
		void onLivingDamage(LivingDamageEvent event);
	}
}
