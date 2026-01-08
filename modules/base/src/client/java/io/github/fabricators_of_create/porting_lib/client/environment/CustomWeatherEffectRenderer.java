package io.github.fabricators_of_create.porting_lib.client.environment;

import io.github.fabricators_of_create.porting_lib.core.annotations.Todo;
import io.github.fabricators_of_create.porting_lib.world.PortingLibEnvironmentAttributes;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.client.renderer.state.WeatherRenderState;
import net.minecraft.world.phys.Vec3;

/**
 * A custom renderer for snow and rain that can be registered using {@link CustomEnvironmentEffectsRendererManager#registerWeatherEffectRenderer}
 * and used with {@link PortingLibEnvironmentAttributes#CUSTOM_WEATHER_EFFECTS}.
 * <p>
 * Custom render state needed for the various render methods must be extracted via {@link WorldRenderEvents#END_EXTRACTION}
 * and stored in the provided {@link LevelRenderState}.
 *
 * @see PortingLibEnvironmentAttributes#CUSTOM_WEATHER_EFFECTS
 */
@Todo
public interface CustomWeatherEffectRenderer {
	RenderStateDataKey<CustomWeatherEffectRenderer> KEY = RenderStateDataKey.create(() -> "porting_lib:custom_weather_effect");

	/**
	 * Renders the snow and rain effects of this dimension.
	 *
	 * @return true to prevent vanilla snow and rain rendering
	 */
	default boolean renderSnowAndRain(LevelRenderState levelRenderState, WeatherRenderState weatherRenderState, MultiBufferSource bufferSource, Vec3 camPos) {
		return false;
	}

	/**
	 * Ticks the rain of this dimension.
	 *
	 * @return true to prevent vanilla rain ticking
	 */
	default boolean tickRain(ClientLevel level, int ticks, Camera camera) {
		return false;
	}
}
