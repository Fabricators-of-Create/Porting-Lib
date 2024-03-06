package io.github.fabricators_of_create.porting_lib.entity.events.player;

import io.github.fabricators_of_create.porting_lib.entity.events.PlayerEvents;
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
 * This event is cancelable.<br>
 * If this event is canceled, the player does not attack the Entity.<br>
 * <br>
 * This event does not have a result.<br>
 * <br>
 **/
public class AttackEntityEvent extends PlayerEvents {
	public static final Event<AttackEntityCallback> ATTACK_ENTITY = EventFactory.createArrayBacked(AttackEntityCallback.class, callbacks -> event -> {
		for (AttackEntityCallback e : callbacks)
			e.onAttackEntity(event);
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
		ATTACK_ENTITY.invoker().onAttackEntity(this);
	}

	@FunctionalInterface
	public interface AttackEntityCallback {
		void onAttackEntity(AttackEntityEvent event);
	}
}
