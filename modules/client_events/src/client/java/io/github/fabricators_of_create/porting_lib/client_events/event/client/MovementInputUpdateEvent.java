package io.github.fabricators_of_create.porting_lib.client_events.event.client;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;
import io.github.fabricators_of_create.porting_lib.core.event.entity.player.PlayerEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.player.ClientInput;
import net.minecraft.world.entity.player.Player;

import org.jetbrains.annotations.ApiStatus;

/**
 * <p>Fired after the player's movement inputs are updated.</p>
 *
 * <p>This event is not cancellable, and does not have a result.</p>
 *
 * <p>This event is fired only on the {@linkplain EnvType#CLIENT logical client}.</p>
 */
public class MovementInputUpdateEvent extends PlayerEvent {
	public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
		for (Callback callback : callbacks)
			callback.onMovementInputUpdate(event);
	});

	private final ClientInput input;

	@ApiStatus.Internal
	public MovementInputUpdateEvent(Player player, ClientInput input) {
		super(player);
		this.input = input;
	}

	/**
	 * {@return the player's movement inputs}
	 */
	public ClientInput getInput() {
		return input;
	}

	@Override
	public MovementInputUpdateEvent sendEvent() {
		EVENT.invoker().onMovementInputUpdate(this);
		return this;
	}

	public interface Callback {
		void onMovementInputUpdate(MovementInputUpdateEvent event);
	}
}
