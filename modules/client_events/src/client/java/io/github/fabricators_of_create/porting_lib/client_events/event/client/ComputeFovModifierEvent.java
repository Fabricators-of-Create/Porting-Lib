package io.github.fabricators_of_create.porting_lib.client_events.event.client;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;
import io.github.fabricators_of_create.porting_lib.core.event.CancellableEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

import org.jetbrains.annotations.ApiStatus;

/**
 * Fired after the field of vision (FOV) modifier for the player is calculated to allow developers to adjust it further.
 *
 * <p>This event is not {@linkplain CancellableEvent cancellable}, and does not have a result.</p>
 *
 * <p>This event is fired only on the {@linkplain EnvType#CLIENT logical client}.</p>
 *
 * @see ViewportEvent.ComputeFov
 */
public class ComputeFovModifierEvent extends BaseEvent {
	private final Player player;
	private final float fovModifier;
	private float newFovModifier;

	@ApiStatus.Internal
	public ComputeFovModifierEvent(Player player, float fovModifier) {
		this.player = player;
		this.fovModifier = fovModifier;
		this.setNewFovModifier((float) Mth.lerp(Minecraft.getInstance().options.fovEffectScale().get(), 1.0F, fovModifier));
	}

	/**
	 * {@return the player affected by this event}
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * {@return the original field of vision (FOV) of the player, before any modifications or interpolation}
	 */
	public float getFovModifier() {
		return fovModifier;
	}

	/**
	 * {@return the current field of vision (FOV) of the player}
	 */
	public float getNewFovModifier() {
		return newFovModifier;
	}

	/**
	 * Sets the new field of vision (FOV) of the player.
	 *
	 * @param newFovModifier the new field of vision (FOV)
	 */
	public void setNewFovModifier(float newFovModifier) {
		this.newFovModifier = newFovModifier;
	}

	public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
		for (Callback callback : callbacks) {
			callback.onComputeFovModifier(event);
		}
	});

	@Override
	public void sendEvent() {
		EVENT.invoker().onComputeFovModifier(this);
	}

	public interface Callback {
		void onComputeFovModifier(ComputeFovModifierEvent event);
	}
}
