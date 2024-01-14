package io.github.fabricators_of_create.porting_lib.event.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.Camera;
import net.minecraft.client.Options;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.GameRenderer;

public class FieldOfViewEvents {
	/**
	 * Allows the field of view to be modified. For example, slowness, sprinting, and flying all modify the FOV.
	 * This is invoked by {@link AbstractClientPlayer#getFieldOfViewModifier()} after all vanilla effects have been applied.
	 * This event is chained and not cancelled - all listeners get a chance to modify the FOV.
	 */
	public static final Event<Modify> MODIFY = EventFactory.createArrayBacked(Modify.class, callbacks -> (player, fov) -> {
		for (Modify callback : callbacks) {
			fov = callback.modifyFov(player, fov);
		}
		return fov;
	});

	/**
	 * Modify the computed base field of view.
	 * This is invoked by {@link GameRenderer#getFov(Camera, float, boolean)} after all vanilla effects have been applied.
	 * This event is chained and not cancelled - all listeners get a chance to modify the FOV.
	 */
	public static final Event<Compute> COMPUTE = EventFactory.createArrayBacked(Compute.class, callbacks -> (renderer, camera, partialTicks, usedFovSetting, fov) -> {
		for (Compute callback : callbacks) {
			fov = callback.getFov(renderer, camera, partialTicks, usedFovSetting, fov);
		}
		return fov;
	});

	public interface Modify {
		/**
		 * Modify the field of view.
		 * @param fov the current FOV, possibly already modified by other callbacks
		 * @return the new FOV, or 'fov' if unchanged
		 */
		float modifyFov(AbstractClientPlayer player, float fov);
	}

	public interface Compute {
		/**
		 * Modify the base FOV.
		 * @param fov the current FOV, possibly already modified by other callbacks
		 * @param usedFovSetting true if {@link Options#fov()} was used in calculating the FOV
		 * @return the new FOV, or 'fov' if unchanged
		 */
		double getFov(GameRenderer renderer, Camera camera, double partialTicks, boolean usedFovSetting, double fov);
	}
}
