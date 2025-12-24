package io.github.fabricators_of_create.porting_lib.client_events.event.client;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.environment.FogEnvironment;
import net.minecraft.world.level.material.FogType;

import org.jetbrains.annotations.ApiStatus;
import org.joml.Vector4f;
import org.jspecify.annotations.Nullable;

/**
 * Fired for hooking into the entity view rendering in {@link GameRenderer}.
 * These can be used for customizing the visual features visible to the player.
 * See the various subclasses for listening to different features.
 *
 * <p>These events are only on the {@linkplain EnvType#CLIENT logical client}.</p>
 *
 * @see RenderFog
 * @see ComputeFogColor
 * @see ComputeCameraAngles
 * @see ComputeFov
 */
public abstract class ViewportEvent extends BaseEvent {
	private final GameRenderer renderer;
	private final Camera camera;
	private final double partialTick;

	@ApiStatus.Internal
	public ViewportEvent(GameRenderer renderer, Camera camera, double partialTick) {
		this.renderer = renderer;
		this.camera = camera;
		this.partialTick = partialTick;
	}

	/**
	 * {@return the game renderer}
	 */
	public GameRenderer getRenderer() {
		return renderer;
	}

	/**
	 * {@return the camera information}
	 */
	public Camera getCamera() {
		return camera;
	}

	/**
	 * {@return the partial tick}
	 */
	public double getPartialTick() {
		return partialTick;
	}

	/**
	 * Fired for <b>rendering</b> custom fog. The plane distances are based on the player's render distance.
	 */
	public static class RenderFog extends ViewportEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (Callback callback : callbacks)
				callback.onRenderFog(event);
		});

		@Nullable
		private final FogEnvironment environment;
		private final FogType type;
		private final FogData fogData;

		@ApiStatus.Internal
		public RenderFog(@Nullable FogEnvironment environment, FogType type, Camera camera, float partialTicks, FogData fogData) {
			super(Minecraft.getInstance().gameRenderer, camera, partialTicks);
			this.environment = environment;
			this.type = type;
			this.fogData = fogData;
			setFarPlaneDistance(fogData.environmentalEnd);
			setNearPlaneDistance(fogData.environmentalStart);
		}

		/**
		 * {@return the fog environment that was applied}
		 */
		@Nullable
		public FogEnvironment getEnvironment() {
			return environment;
		}

		/**
		 * {@return the type of fog being rendered}
		 */
		public FogType getType() {
			return type;
		}

		/**
		 * {@return the distance to the far plane where the fog ends}
		 */
		public float getFarPlaneDistance() {
			return fogData.environmentalEnd;
		}

		/**
		 * {@return the distance to the near plane where the fog starts}
		 */
		public float getNearPlaneDistance() {
			return fogData.environmentalStart;
		}

		/**
		 * The fog parameters that are passed to the shaders. This object is mutable.
		 */
		public FogData getFogData() {
			return fogData;
		}

		/**
		 * Sets the distance to the far plane of the fog.
		 *
		 * @param distance the new distance to the far place
		 * @see #scaleFarPlaneDistance(float)
		 */
		public void setFarPlaneDistance(float distance) {
			fogData.environmentalEnd = distance;
		}

		/**
		 * Sets the distance to the near plane of the fog.
		 *
		 * @param distance the new distance to the near plane
		 * @see #scaleNearPlaneDistance(float)
		 */
		public void setNearPlaneDistance(float distance) {
			fogData.environmentalStart = distance;
		}

		/**
		 * Scales the distance to the far plane of the fog by a given factor.
		 *
		 * @param factor the factor to scale the far plane distance by
		 */
		public void scaleFarPlaneDistance(float factor) {
			fogData.environmentalEnd *= factor;
		}

		/**
		 * Scales the distance to the near plane of the fog by a given factor.
		 *
		 * @param factor the factor to scale the near plane distance by
		 */
		public void scaleNearPlaneDistance(float factor) {
			fogData.environmentalStart *= factor;
		}

		@Override
		public RenderFog sendEvent() {
			EVENT.invoker().onRenderFog(this);
			return this;
		}

		public interface Callback {
			void onRenderFog(RenderFog event);
		}
	}

	/**
	 * Fired for customizing the <b>color</b> of the fog visible to the player.
	 */
	public static class ComputeFogColor extends ViewportEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (Callback callback : callbacks)
				callback.onComputeFogColor(event);
		});

		private Vector4f fogColor;

		@ApiStatus.Internal
		public ComputeFogColor(Camera camera, float partialTicks, float red, float green, float blue) {
			super(Minecraft.getInstance().gameRenderer, camera, partialTicks);
			this.fogColor = new Vector4f(red, green, blue, 1f);
			this.setRed(red);
			this.setGreen(green);
			this.setBlue(blue);
		}

		@ApiStatus.Internal
		public ComputeFogColor(Camera camera, float partialTicks, Vector4f fogColor) {
			super(Minecraft.getInstance().gameRenderer, camera, partialTicks);
			this.setRed(fogColor.x);
			this.setGreen(fogColor.y);
			this.setBlue(fogColor.z);
		}

		/**
		 * {@return the red color value of the fog}
		 */
		public float getRed() {
			return fogColor.x;
		}

		/**
		 * Sets the new red color value of the fog.
		 *
		 * @param red the new red color value
		 */
		public void setRed(float red) {
			this.fogColor.x = red;
		}

		/**
		 * {@return the green color value of the fog}
		 */
		public float getGreen() {
			return fogColor.y;
		}

		/**
		 * Sets the new green color value of the fog.
		 *
		 * @param green the new blue color value
		 */
		public void setGreen(float green) {
			this.fogColor.y = green;
		}

		/**
		 * {@return the blue color value of the fog}
		 */
		public float getBlue() {
			return fogColor.z;
		}

		/**
		 * Sets the new blue color value of the fog.
		 *
		 * @param blue the new blue color value
		 */
		public void setBlue(float blue) {
			this.fogColor.z = blue;
		}

		@Override
		public ComputeFogColor sendEvent() {
			EVENT.invoker().onComputeFogColor(this);
			return this;
		}

		public interface Callback {
			void onComputeFogColor(ComputeFogColor event);
		}
	}

	/**
	 * Fired to allow altering the angles of the player's camera.
	 * This can be used to alter the player's view for different effects, such as applying roll.
	 */
	public static class ComputeCameraAngles extends ViewportEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (Callback callback : callbacks)
				callback.onComputeCameraAngles(event);
		});

		private float yaw;
		private float pitch;
		private float roll;

		@ApiStatus.Internal
		public ComputeCameraAngles(Camera camera, double renderPartialTicks, float yaw, float pitch, float roll) {
			super(Minecraft.getInstance().gameRenderer, camera, renderPartialTicks);
			this.setYaw(yaw);
			this.setPitch(pitch);
			this.setRoll(roll);
		}

		/**
		 * {@return the yaw of the player's camera}
		 */
		public float getYaw() {
			return yaw;
		}

		/**
		 * Sets the yaw of the player's camera.
		 *
		 * @param yaw the new yaw
		 */
		public void setYaw(float yaw) {
			this.yaw = yaw;
		}

		/**
		 * {@return the pitch of the player's camera}
		 */
		public float getPitch() {
			return pitch;
		}

		/**
		 * Sets the pitch of the player's camera.
		 *
		 * @param pitch the new pitch
		 */
		public void setPitch(float pitch) {
			this.pitch = pitch;
		}

		/**
		 * {@return the roll of the player's camera}
		 */
		public float getRoll() {
			return roll;
		}

		/**
		 * Sets the roll of the player's camera.
		 *
		 * @param roll the new roll
		 */
		public void setRoll(float roll) {
			this.roll = roll;
		}

		@Override
		public ComputeCameraAngles sendEvent() {
			EVENT.invoker().onComputeCameraAngles(this);
			return this;
		}

		public interface Callback {
			void onComputeCameraAngles(ComputeCameraAngles event);
		}
	}

	/**
	 * Fired for altering the raw field of view (FOV).
	 * This is after the FOV settings are applied, and before modifiers such as the Nausea effect.
	 *
	 * @see ComputeFovModifierEvent
	 */
	public static class ComputeFov extends ViewportEvent {
		public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
			for (Callback callback : callbacks)
				callback.onComputeFov(event);
		});

		private final boolean usedConfiguredFov;
		private float fov;

		@ApiStatus.Internal
		public ComputeFov(GameRenderer renderer, Camera camera, float renderPartialTicks, float fov, boolean usedConfiguredFov) {
			super(renderer, camera, renderPartialTicks);
			this.usedConfiguredFov = usedConfiguredFov;
			this.setFOV(fov);
		}

		/**
		 * {@return the raw field of view value}
		 */
		public float getFOV() {
			return fov;
		}

		/**
		 * Sets the field of view value.
		 *
		 * @param fov the new FOV value
		 */
		public void setFOV(float fov) {
			this.fov = fov;
		}

		/**
		 * {@return whether the base fov value started with a constant or was sourced from the fov set in the options}
		 */
		public boolean usedConfiguredFov() {
			return usedConfiguredFov;
		}

		@Override
		public ComputeFov sendEvent() {
			EVENT.invoker().onComputeFov(this);
			return this;
		}

		public interface Callback {
			void onComputeFov(ComputeFov event);
		}
	}
}
