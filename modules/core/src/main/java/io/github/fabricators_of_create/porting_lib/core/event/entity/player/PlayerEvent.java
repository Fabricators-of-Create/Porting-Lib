package io.github.fabricators_of_create.porting_lib.core.event.entity.player;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;
import io.github.fabricators_of_create.porting_lib.core.event.entity.living.LivingEvent;
import net.minecraft.world.entity.player.Player;

/**
 * PlayerEvent is fired whenever an event involving a {@link Player} occurs. <br>
 * If a method utilizes this {@link BaseEvent} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 **/
public abstract class PlayerEvent extends LivingEvent {
	private final Player player;

	public PlayerEvent(Player player) {
		super(player);
		this.player = player;
	}

	@Override
	public Player getEntity() {
		return player;
	}
}
