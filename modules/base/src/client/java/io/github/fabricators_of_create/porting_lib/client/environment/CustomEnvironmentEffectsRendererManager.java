package io.github.fabricators_of_create.porting_lib.client.environment;

import io.github.fabricators_of_create.porting_lib.world.PortingLibEnvironmentAttributes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import org.jspecify.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Manager for custom renderers referred to by {@link net.minecraft.world.attribute.EnvironmentAttribute}.
 */
public final class CustomEnvironmentEffectsRendererManager {
	private static final Map<Identifier, CustomCloudsRenderer> CUSTOM_CLOUD_RENDERERS = new IdentityHashMap<>();
	private static final Map<Identifier, CustomSkyboxRenderer> CUSTOM_SKYBOX_RENDERERS = new IdentityHashMap<>();
	private static final Map<Identifier, CustomWeatherEffectRenderer> CUSTOM_WEATHER_EFFECT_RENDERERS = new IdentityHashMap<>();

	/**
	 * Registers the renderer for a given custom clouds type.
	 *
	 * @see PortingLibEnvironmentAttributes#CUSTOM_CLOUDS
	 */
	public static void registerCloudRenderer(Identifier id, CustomCloudsRenderer effects) {
		if (PortingLibEnvironmentAttributes.DEFAULT_CUSTOM_CLOUDS.equals(id)) {
			throw new IllegalArgumentException("You cannot register a renderer for the default clouds");
		}
		CUSTOM_CLOUD_RENDERERS.put(id, effects);
	}

	/**
	 * Registers the renderer for a given custom skybox type.
	 *
	 * @see PortingLibEnvironmentAttributes#CUSTOM_SKYBOX
	 */
	public static void registerSkyboxRenderer(Identifier id, CustomSkyboxRenderer effects) {
		if (PortingLibEnvironmentAttributes.DEFAULT_CUSTOM_SKYBOX.equals(id)) {
			throw new IllegalArgumentException("You cannot register a renderer for the default skybox");
		}
		CUSTOM_SKYBOX_RENDERERS.put(id, effects);
	}

	/**
	 * Registers the renderer for a given custom weather effects type.
	 *
	 * @see PortingLibEnvironmentAttributes#CUSTOM_WEATHER_EFFECTS
	 */
	public static void registerWeatherEffectRenderer(Identifier id, CustomWeatherEffectRenderer effects) {
		if (PortingLibEnvironmentAttributes.DEFAULT_CUSTOM_WEATHER_EFFECTS.equals(id)) {
			throw new IllegalArgumentException("You cannot register a renderer for the default weather effects");
		}
		CUSTOM_WEATHER_EFFECT_RENDERERS.put(id, effects);
	}

	/**
	 * Finds the {@link CustomCloudsRenderer} for a given identifier, or null if none is registered.
	 */
	public static @Nullable CustomCloudsRenderer getCustomCloudsRenderer(Identifier id) {
		if (PortingLibEnvironmentAttributes.DEFAULT_CUSTOM_CLOUDS.equals(id)) {
			return null;
		}
		return Objects.requireNonNull(CUSTOM_CLOUD_RENDERERS).get(id);
	}

	/**
	 * Finds the {@link CustomCloudsRenderer} to use for the given position in the given level.
	 */
	public static @Nullable CustomCloudsRenderer getCustomCloudsRenderer(Level level, Vec3 position) {
		var id = level.environmentAttributes().getValue(PortingLibEnvironmentAttributes.CUSTOM_CLOUDS, position);
		return getCustomCloudsRenderer(id);
	}

	/**
	 * Finds the {@link CustomSkyboxRenderer} for a given identifier, or null if none is registered.
	 */
	public static @Nullable CustomSkyboxRenderer getCustomSkyboxRenderer(Identifier id) {
		if (PortingLibEnvironmentAttributes.DEFAULT_CUSTOM_SKYBOX.equals(id)) {
			return null;
		}
		return Objects.requireNonNull(CUSTOM_SKYBOX_RENDERERS).get(id);
	}

	/**
	 * Finds the {@link CustomSkyboxRenderer} to use for the given position in the given level.
	 */
	public static @Nullable CustomSkyboxRenderer getCustomSkyboxRenderer(Level level, Vec3 position) {
		var id = level.environmentAttributes().getValue(PortingLibEnvironmentAttributes.CUSTOM_SKYBOX, position);
		return getCustomSkyboxRenderer(id);
	}

	/**
	 * Finds the {@link CustomWeatherEffectRenderer} for a given identifier, or null if none is registered.
	 */
	public static @Nullable CustomWeatherEffectRenderer getCustomWeatherEffectRenderer(Identifier id) {
		if (PortingLibEnvironmentAttributes.DEFAULT_CUSTOM_WEATHER_EFFECTS.equals(id)) {
			return null;
		}
		return Objects.requireNonNull(CUSTOM_WEATHER_EFFECT_RENDERERS).get(id);
	}

	/**
	 * Finds the {@link CustomWeatherEffectRenderer} to use for the given position in the given level.
	 */
	public static @Nullable CustomWeatherEffectRenderer getCustomWeatherEffectRenderer(Level level, Vec3 position) {
		var id = level.environmentAttributes().getValue(PortingLibEnvironmentAttributes.CUSTOM_WEATHER_EFFECTS, position);
		return getCustomWeatherEffectRenderer(id);
	}

	private CustomEnvironmentEffectsRendererManager() {}
}
