package io.github.fabricators_of_create.porting_lib.entity.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

/**
 * The ShieldBlockEvent is fired when an entity successfully blocks with a shield.<br>
 * Cancelling this event will have the same impact as if the shield was not eligible to block.<br>
 * The damage blocked cannot be set lower than zero or greater than the original value.<br>
 * Note: The shield item stack "should" be available from {@link LivingEntity#getUseItem()}
 * at least for players.
 */
public class ShieldBlockEvent extends EntityEvents {
	public static Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
		for (Callback e : callbacks)
			e.onShieldBlock(event);
	});

	private final DamageSource source;
	private final float originalBlocked;
	private float dmgBlocked;
	private boolean shieldTakesDamage = true;

	public ShieldBlockEvent(LivingEntity blocker, DamageSource source, float blocked) {
		super(blocker);
		this.source = source;
		this.originalBlocked = blocked;
		this.dmgBlocked = blocked;
	}

	@Override
	public LivingEntity getEntity() {
		return (LivingEntity) super.getEntity();
	}

	/**
	 * @return The damage source.
	 */
	public DamageSource getDamageSource() {
		return this.source;
	}

	/**
	 * @return The original amount of damage blocked, which is the same as the original
	 * incoming damage value.
	 */
	public float getOriginalBlockedDamage() {
		return this.originalBlocked;
	}

	/**
	 * @return The current amount of damage blocked, as a result of this event.
	 */
	public float getBlockedDamage() {
		return this.dmgBlocked;
	}

	/**
	 * Controls if {@link LivingEntity#hurtCurrentlyUsedShield} is called.
	 * @return If the shield item will take durability damage or not.
	 */
	public boolean shieldTakesDamage() {
		return this.shieldTakesDamage;
	}

	/**
	 * Set how much damage is blocked by this action.<br>
	 * Note that initially the blocked amount is the entire attack.<br>
	 */
	public void setBlockedDamage(float blocked) {
		this.dmgBlocked = Mth.clamp(blocked, 0, this.originalBlocked);
	}

	/**
	 * Set if the shield will take durability damage or not.
	 */
	public void setShieldTakesDamage(boolean damage) {
		this.shieldTakesDamage = damage;
	}

	@Override
	public void sendEvent() {
		EVENT.invoker().onShieldBlock(this);
	}

	@FunctionalInterface
	public interface Callback {
		void onShieldBlock(ShieldBlockEvent event);
	}
}
