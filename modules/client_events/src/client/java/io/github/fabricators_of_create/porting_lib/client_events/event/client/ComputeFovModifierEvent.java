package io.github.fabricators_of_create.porting_lib.client_events.event.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;
import io.github.fabricators_of_create.porting_lib.core.event.CancellableEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
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
	public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
		for (Callback callback : callbacks) {
			callback.onComputeFovModifier(event);
		}
	});

	private final Player player;
	private final float fovModifier;
	private final float fovScale;
	private float newFovModifier;

	@ApiStatus.Internal
	public ComputeFovModifierEvent(Player player, float fovModifier, float fovScale) {
		this.player = player;
		this.fovModifier = fovModifier;
		this.fovScale = fovScale;
		this.setNewFovModifier(Mth.lerp(fovScale, 1.0F, fovModifier));
	}

	@ApiStatus.Internal
	public ComputeFovModifierEvent(Player player, float fovModifier, float fovScale, float start, Operation<Float> operation) {
		this.player = player;
		this.fovModifier = fovModifier;
		this.fovScale = fovScale;
		this.setNewFovModifier(operation.call(fovScale, start, fovModifier));
	}

//	@ApiStatus.Internal
//	public ComputeFovModifierEvent(Player player, float fovModifier, float fovScale) {
//		this.player = player;
//		this.fovModifier = fovModifier;
//		this.fovScale = fovScale;
//		this.setNewFovModifier(Mth.lerp(fovScale, 1.0F, fovModifier));
//	}

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
	 * {@return the FOV scale to use for interpolating the final FOV modifier}
	 */
	public float getFovScale() {
		return fovScale;
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

	@Override
	public ComputeFovModifierEvent sendEvent() {
		EVENT.invoker().onComputeFovModifier(this);
		return this;
	}

	public interface Callback {
		void onComputeFovModifier(ComputeFovModifierEvent event);
	}
}
