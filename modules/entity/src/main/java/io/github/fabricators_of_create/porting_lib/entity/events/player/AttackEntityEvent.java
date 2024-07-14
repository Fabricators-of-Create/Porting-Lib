package io.github.fabricators_of_create.porting_lib.entity.events.player;

import io.github.fabricators_of_create.porting_lib.core.event.CancellableEvent;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

/**
 * AttackEntityEvent is fired when a player attacks an Entity.<br>
 * This event is fired whenever a player attacks an Entity in
 * {@link Player#attack(Entity)}.<br>
 * <br>
 * {@link #target} contains the Entity that was damaged by the player. <br>
 * <br>
 * This event is {@link CancellableEvent}.<br>
 * If this event is canceled, the player does not attack the Entity.<br>
 **/
public class AttackEntityEvent extends PlayerEvent implements CancellableEvent {
	public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
		for (final Callback callback : callbacks)
			callback.onAttackEntity(event);
	});

	private final Entity target;

	public AttackEntityEvent(Player player, Entity target) {
		super(player);
		this.target = target;
	}

	public Entity getTarget() {
		return target;
	}

	@Override
	public void sendEvent() {
		EVENT.invoker().onAttackEntity(this);
	}

	public interface Callback {
		void onAttackEntity(AttackEntityEvent event);
	}
}
