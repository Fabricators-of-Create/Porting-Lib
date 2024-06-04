package io.github.fabricators_of_create.porting_lib.entity.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

/**
 * This event is fired whenever a player attacks an Entity in
 * {@link Player#attack(Entity)}.<br>
 * <br>
 * This event is not cancelable.<br>
 * <br>
 * This event has a result.<br>
 * DEFAULT: means the vanilla logic will determine if this a critical hit.<br>
 * DENY: it will not be a critical hit but the player still will attack<br>
 * ALLOW: this attack is forced to be critical
 * <br>
 **/
public class CriticalHitEvent extends PlayerEvents {
	public static final Event<CriticalCallback> CRITICAL_HIT = EventFactory.createArrayBacked(CriticalCallback.class, callbacks -> event -> {
		for (CriticalCallback callback : callbacks)
			callback.onCriticalHit(event);
	});
	private float damageModifier;
	private final float oldDamageModifier;
	private final Entity target;
	private final boolean vanillaCritical;

	public CriticalHitEvent(Player player, Entity target, float damageModifier, boolean vanillaCritical) {
		super(player);
		this.target = target;
		this.damageModifier = damageModifier;
		this.oldDamageModifier = damageModifier;
		this.vanillaCritical = vanillaCritical;
	}

	/**
	 * The Entity that was damaged by the player.
	 */
	public Entity getTarget() {
		return target;
	}

	/**
	 * This set the damage multiplier for the hit.
	 * If you set it to 0, then the particles are still generated but damage is not done.
	 */
	public void setDamageModifier(float mod) {
		this.damageModifier = mod;
	}

	/**
	 * The damage modifier for the hit.<br>
	 * This is by default 1.5F for ciritcal hits and 1F for normal hits .
	 */
	public float getDamageModifier() {
		return this.damageModifier;
	}

	/**
	 * The orignal damage modifier for the hit wthout any changes.<br>
	 * This is 1.5F for ciritcal hits and 1F for normal hits .
	 */
	public float getOldDamageModifier() {
		return this.oldDamageModifier;
	}

	/**
	 * Returns true if this hit was critical by vanilla
	 */
	public boolean isVanillaCritical() {
		return vanillaCritical;
	}

	@Override
	public void sendEvent() {
		CRITICAL_HIT.invoker().onCriticalHit(this);
	}

	@FunctionalInterface
	public interface CriticalCallback {
		void onCriticalHit(CriticalHitEvent event);
	}
}
