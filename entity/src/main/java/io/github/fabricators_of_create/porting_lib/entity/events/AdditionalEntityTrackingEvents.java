package io.github.fabricators_of_create.porting_lib.entity.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class AdditionalEntityTrackingEvents {
	/**
	 * Counterpart event for {@link EntityTrackingEvents#START_TRACKING}
	 */
	public static final Event<AfterStartTracking> AFTER_START_TRACKING = EventFactory.createArrayBacked(AfterStartTracking.class, callbacks -> (entity, player) -> {
		for (AfterStartTracking callback : callbacks)
			callback.afterStartTracking(entity, player);
	});

	@FunctionalInterface
	public interface AfterStartTracking {
		void afterStartTracking(Entity entity, ServerPlayer player);
	}

	/**
	 * Counterpart event for {@link EntityTrackingEvents#STOP_TRACKING}
	 */
	public static final Event<StopTracking> BEFORE_STOP_TRACKING = EventFactory.createArrayBacked(StopTracking.class, callbacks -> (entity, player) -> {
		for (StopTracking callback : callbacks)
			callback.beforeStopTracking(entity, player);
	});

	@FunctionalInterface
	public interface StopTracking {
		void beforeStopTracking(Entity entity, ServerPlayer player);
	}
}
