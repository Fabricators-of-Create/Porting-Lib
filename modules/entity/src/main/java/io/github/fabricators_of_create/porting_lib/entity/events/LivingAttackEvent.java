package io.github.fabricators_of_create.porting_lib.entity.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

/**
 * LivingAttackEvent is fired when a living Entity is attacked. <br>
 * This event is fired whenever an Entity is attacked in
 * {@link LivingEntity#hurt(DamageSource, float)} and
 * {@link Player#hurt(DamageSource, float)}. <br>
 * <br>
 * {@link #source} contains the DamageSource of the attack. <br>
 * {@link #amount} contains the amount of damage dealt to the entity. <br>
 * <br>
 * This event is cancelable.<br>
 * If this event is canceled, the Entity does not take attack damage.<br>
 * <br>
 * This event does not have a result.<br>
 * <br>
 **/
public class LivingAttackEvent extends LivingEntityEvents {
	public static final Event<LivingAttackCallback> ATTACK = EventFactory.createArrayBacked(LivingAttackCallback.class, callbacks -> event -> {
		for (LivingAttackCallback e : callbacks)
			e.onLivingAttack(event);
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
		ATTACK.invoker().onLivingAttack(this);
	}

	@FunctionalInterface
	public interface LivingAttackCallback {
		void onLivingAttack(LivingAttackEvent event);
	}
}
