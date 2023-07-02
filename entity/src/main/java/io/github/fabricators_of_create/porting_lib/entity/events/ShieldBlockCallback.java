package io.github.fabricators_of_create.porting_lib.entity.events;

import io.github.fabricators_of_create.porting_lib.core.event.object.CancellableEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface ShieldBlockCallback {
	/**
	 * This event is fired when an attack is blocked with a shield. Cancelling it will prevent blocking.
	 * Listeners have the ability to change the amount of damage blocked and determine whether the shield
	 * item gets damaged or not.
	 */
	Event<ShieldBlockCallback> EVENT = EventFactory.createArrayBacked(ShieldBlockCallback.class, callbacks -> event -> {
		for (ShieldBlockCallback e : callbacks) {
			e.onShieldBlock(event);
			if (event.isCancelled())
				return;
		}
	});

	void onShieldBlock(ShieldBlockEvent event);

	class ShieldBlockEvent extends CancellableEvent {
		public final LivingEntity blocker;
		public final ItemStack shield;
		public final DamageSource source;

		public float damageBlocked;
		public boolean damageShield = true;

		public ShieldBlockEvent(LivingEntity blocker, ItemStack shield, DamageSource source, float damageBlocked) {
			this.blocker = blocker;
			this.shield = shield;
			this.source = source;
			this.damageBlocked = damageBlocked;
		}
	}
}
