package io.github.fabricators_of_create.porting_lib.entity.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.Entity;

import org.apache.commons.lang3.function.TriFunction;

public class EntityMountEvents {
	/**
	 * Register one listener to both events. The listener receives the vehicle, the passenger,
	 * and a boolean determining if the passenger is mounting or dismounting.
	 * Returning false will cancel either event.
	 */
	public static void registerForBoth(TriFunction<Entity, Entity, Boolean, Boolean> listener) {
		MOUNT.register((vehicle, passenger) -> listener.apply(vehicle, passenger, true));
		DISMOUNT.register((vehicle, passenger) -> listener.apply(vehicle, passenger, false));
	}

	/**
	 * Fired when an entity tries to start riding another.
	 */
	public static final Event<Mount> MOUNT = EventFactory.createArrayBacked(Mount.class, callbacks -> (vehicle, passenger) -> {
		for (Mount callback : callbacks) {
			if (!callback.canStartRiding(vehicle, passenger))
				return false;
		}
		return true;
	});

	@FunctionalInterface
	public interface Mount {
		/**
		 * @return false to cancel mounting
		 */
		boolean canStartRiding(Entity vehicle, Entity passenger);
	}

	/**
	 * Fired when an entity tries to stop riding another.
	 */
	public static final Event<Dismount> DISMOUNT = EventFactory.createArrayBacked(Dismount.class, callbacks -> (vehicle, passenger) -> {
		for (Dismount callback : callbacks) {
			if (!callback.onStopRiding(vehicle, passenger))
				return false;
		}
		return true;
	});

	@FunctionalInterface
	public interface Dismount {
		/**
		 * @return false to cancel dismounting
		 */
		boolean onStopRiding(Entity vehicle, Entity passenger);
	}
}
