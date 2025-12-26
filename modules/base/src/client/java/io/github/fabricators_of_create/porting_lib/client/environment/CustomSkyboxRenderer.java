package io.github.fabricators_of_create.porting_lib.client.environment;

import io.github.fabricators_of_create.porting_lib.core.annotations.NotImplemented;
import io.github.fabricators_of_create.porting_lib.world.PortingLibEnvironmentAttributes;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.client.renderer.state.SkyRenderState;

import org.joml.Matrix4f;

/**
 * A custom skybox renderer that can be registered using {@link CustomEnvironmentEffectsRendererManager#registerSkyboxRenderer}
 * and used with {@link PortingLibEnvironmentAttributes#CUSTOM_SKYBOX}.
 * <p>
 * Custom render state needed for the various render methods must be extracted via {@link WorldRenderEvents#END_EXTRACTION}
 * and stored in the provided {@link LevelRenderState}.
 *
 * @see PortingLibEnvironmentAttributes#CUSTOM_SKYBOX
 */
@NotImplemented // Forge patches the levelRenderState and modelViewMatrix while vanilla doesn't
public interface CustomSkyboxRenderer {
	RenderStateDataKey<CustomSkyboxRenderer> KEY = RenderStateDataKey.create(() -> "porting_lib:custom_skybox");

	/**
	 * Renders the sky of this dimension.
	 *
	 * @return true to prevent vanilla sky rendering
	 */
	default boolean renderSky(LevelRenderState levelRenderState, SkyRenderState skyRenderState, Matrix4f modelViewMatrix, Runnable setupFog) {
		return false;
	}
}
