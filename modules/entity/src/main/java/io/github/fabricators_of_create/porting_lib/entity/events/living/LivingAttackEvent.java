package io.github.fabricators_of_create.porting_lib.entity.events.living;

import io.github.fabricators_of_create.porting_lib.core.event.CancellableEvent;
import io.github.fabricators_of_create.porting_lib.entity.EntityHooks;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * LivingAttackEvent is fired when a living Entity is attacked. <br>
 * This event is fired whenever an Entity is attacked in
 * {@link LivingEntity#hurt(DamageSource, float)} and
 * {@link Player#hurt(DamageSource, float)}. <br>
 * <br>
 * This event is fired via the {@link EntityHooks#onLivingAttack(LivingEntity, DamageSource, float)}.<br>
 * <br>
 * {@link #source} contains the DamageSource of the attack. <br>
 * {@link #amount} contains the amount of damage dealt to the entity. <br>
 * <br>
 * This event is {@link CancellableEvent}.<br>
 * If this event is canceled, the Entity does not take attack damage.<br>
 **/
public class LivingAttackEvent extends LivingEvent implements CancellableEvent {
	public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
		for (Callback callback : callbacks)
			callback.onLivingAttack(event);
	});

	private final DamageSource source;
	private final float amount;

	public LivingAttackEvent(LivingEntity entity, DamageSource source, float amount) {
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

	@Override
	public void sendEvent() {
		EVENT.invoker().onLivingAttack(this);
	}

	public interface Callback {
		void onLivingAttack(LivingAttackEvent event);
	}
}
