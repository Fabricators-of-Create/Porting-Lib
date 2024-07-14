package io.github.fabricators_of_create.porting_lib.entity.events.living;

import io.github.fabricators_of_create.porting_lib.core.event.CancellableEvent;
import io.github.fabricators_of_create.porting_lib.entity.EntityHooks;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

/**
 * LivingKnockBackEvent is fired when a living entity is about to be knocked back. <br>
 * This event is fired whenever an Entity is knocked back in
 * {@link LivingEntity#hurt(DamageSource, float)},
 * {@code LivingEntity#blockUsingShield(LivingEntity)},
 * {@link Mob#doHurtTarget(Entity)} and
 * {@link Player#attack(Entity)} <br>
 * <br>
 * This event is fired via {@link EntityHooks#onLivingKnockBack(LivingEntity, float, double, double)} .<br>
 * <br>
 * {@link #strength} contains the strength of the knock back. <br>
 * {@link #ratioX} contains the x ratio of the knock back. <br>
 * {@link #ratioZ} contains the z ratio of the knock back. <br>
 * <br>
 * This event is {@link CancellableEvent}.<br>
 * If this event is canceled, the entity is not knocked back.<br>
 **/
public class LivingKnockBackEvent extends LivingEvent implements CancellableEvent {
	public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
		for (final Callback callback : callbacks)
			callback.onLivingKnockBack(event);
	});

	protected float strength;
	protected double ratioX, ratioZ;
	protected final float originalStrength;
	protected final double originalRatioX, originalRatioZ;

	public LivingKnockBackEvent(LivingEntity target, float strength, double ratioX, double ratioZ) {
		super(target);
		this.strength = this.originalStrength = strength;
		this.ratioX = this.originalRatioX = ratioX;
		this.ratioZ = this.originalRatioZ = ratioZ;
	}

	public float getStrength() {
		return this.strength;
	}

	public double getRatioX() {
		return this.ratioX;
	}

	public double getRatioZ() {
		return this.ratioZ;
	}

	public float getOriginalStrength() {
		return this.originalStrength;
	}

	public double getOriginalRatioX() {
		return this.originalRatioX;
	}

	public double getOriginalRatioZ() {
		return this.originalRatioZ;
	}

	public void setStrength(float strength) {
		this.strength = strength;
	}

	public void setRatioX(double ratioX) {
		this.ratioX = ratioX;
	}

	public void setRatioZ(double ratioZ) {
		this.ratioZ = ratioZ;
	}

	@Override
	public void sendEvent() {
		EVENT.invoker().onLivingKnockBack(this);
	}

	public interface Callback {
		void onLivingKnockBack(LivingKnockBackEvent event);
	}
}
