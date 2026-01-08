package io.github.fabricators_of_create.porting_lib.client.environment;

import io.github.fabricators_of_create.porting_lib.core.annotations.Todo;
import io.github.fabricators_of_create.porting_lib.world.PortingLibEnvironmentAttributes;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.world.phys.Vec3;

import org.joml.Matrix4f;

/**
 * A custom cloud renderer that can be registered using {@link CustomEnvironmentEffectsRendererManager#registerCloudRenderer)}
 * and used with {@link PortingLibEnvironmentAttributes#CUSTOM_CLOUDS}.
 * <p>
 * Custom render state needed for the various render methods must be extracted via {@link WorldRenderEvents#END_EXTRACTION}
 * and stored in the provided {@link LevelRenderState}.
 *
 * @see PortingLibEnvironmentAttributes#CUSTOM_CLOUDS
 */
@Todo // Forge patches the levelRenderState and modelViewMatrix while vanilla doesn't
public interface CustomCloudsRenderer {
	RenderStateDataKey<CustomCloudsRenderer> KEY = RenderStateDataKey.create(() -> "porting_lib:custom_clouds_renderer");

	/**
	 * Renders the clouds of this dimension.
	 *
	 * @return true to prevent vanilla cloud rendering
	 */
	default boolean renderClouds(LevelRenderState levelRenderState, Vec3 camPos, CloudStatus cloudStatus, int cloudColor, float cloudHeight, Matrix4f modelViewMatrix) {
		return false;
	}
}
