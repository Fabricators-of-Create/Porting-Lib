package io.github.fabricators_of_create.porting_lib.event.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;

/**
 * Callback called when the GameRenderer sets up the Camera.
 */
public interface CameraSetupCallback {
	Event<CameraSetupCallback> EVENT = EventFactory.createArrayBacked(CameraSetupCallback.class, callbacks -> (info) -> {
		for (CameraSetupCallback e : callbacks) {
			if (e.onCameraSetup(info)) {
				return true;
			}
		}
		return false;
	});

	/**
	 * @param info holds the camera data. Modify its fields.
	 * @return true to cancel further processing
	 */
	boolean onCameraSetup(CameraInfo info);

	class CameraInfo {
		public final GameRenderer renderer;
		public final Camera camera;
		public final double partialTicks;
		public float yaw; // yRot
		public float pitch; // xRot
		public float roll; // zRot

		public CameraInfo(GameRenderer renderer, Camera camera, double partialTicks, float yaw, float pitch, float roll) {
			this.renderer = renderer;
			this.camera = camera;
			this.partialTicks = partialTicks;
			this.yaw = yaw;
			this.pitch = pitch;
			this.roll = roll;
		}
	}
}
