package io.github.fabricators_of_create.porting_lib.event.client;

import com.mojang.blaze3d.shaders.FogShape;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.level.material.FogType;

@Environment(EnvType.CLIENT)
public class FogEvents {
	public static final Event<SetDensity> SET_DENSITY = EventFactory.createArrayBacked(SetDensity.class, callbacks -> (info, density) -> {
		for (SetDensity callback : callbacks) {
			return callback.setDensity(info, density);
		}
		return density;
	});

	public static final Event<SetColor> SET_COLOR = EventFactory.createArrayBacked(SetColor.class, callbacks -> (data, partialTicks) -> {
		for (SetColor callback : callbacks) {
			callback.setColor(data, partialTicks);
		}
	});

	public static final Event<RenderFog> RENDER_FOG = EventFactory.createArrayBacked(RenderFog.class, callbacks -> (mode, type, camera, partialTick, renderDistance, nearDistance, farDistance, shape, fogData) -> {
		for (RenderFog callback : callbacks) {
			if (callback.onFogRender(mode, type, camera, partialTick, renderDistance, nearDistance, farDistance, shape, fogData))
				return true;
		}
		return false;
	});

	private FogEvents() {
	}

	@FunctionalInterface
	public interface SetDensity {
		float setDensity(Camera activeRenderInfo, float density);
	}

	@FunctionalInterface
	public interface SetColor {
		void setColor(ColorData d, float partialTicks);
	}

	@FunctionalInterface
	public interface RenderFog {
		boolean onFogRender(FogRenderer.FogMode mode, FogType type, Camera camera, float partialTick, float renderDistance, float nearDistance, float farDistance, FogShape shape, FogData fogData);
	}

	public static class FogData {
		private float farPlaneDistance;
		private float nearPlaneDistance;
		private FogShape fogShape;

		public FogData(float nearPlaneDistance, float farPlaneDistance, FogShape fogShape) {
			setFarPlaneDistance(farPlaneDistance);
			setNearPlaneDistance(nearPlaneDistance);
			setFogShape(fogShape);
		}

		public float getFarPlaneDistance() {
			return farPlaneDistance;
		}

		public float getNearPlaneDistance() {
			return nearPlaneDistance;
		}

		public FogShape getFogShape() {
			return fogShape;
		}

		public void setFarPlaneDistance(float distance) {
			farPlaneDistance = distance;
		}

		public void setNearPlaneDistance(float distance) {
			nearPlaneDistance = distance;
		}

		public void setFogShape(FogShape shape) {
			fogShape = shape;
		}

		public void scaleFarPlaneDistance(float factor) {
			farPlaneDistance *= factor;
		}

		public void scaleNearPlaneDistance(float factor) {
			nearPlaneDistance *= factor;
		}
	}

	public static class ColorData {
		private final Camera camera;
		private float red;
		private float green;
		private float blue;

		public ColorData(Camera camera, float r, float g, float b) {
			this.camera = camera;
			this.red = r;
			this.green = g;
			this.blue = b;
		}

		public Camera getCamera() {
			return camera;
		}

		public float getRed() {
			return red;
		}

		public float getGreen() {
			return green;
		}

		public float getBlue() {
			return blue;
		}

		public void setRed(float red) {
			this.red = red;
		}

		public void setGreen(float green) {
			this.green = green;
		}

		public void setBlue(float blue) {
			this.blue = blue;
		}
	}
}
