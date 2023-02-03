package io.github.fabricators_of_create.porting_lib.extensions.extensions;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;

public interface DimensionSpecialEffectsExtensions {
	/**
	 * Ticks the rain of this dimension.
	 *
	 * @return true to prevent vanilla rain ticking
	 */
	default boolean tickRain(ClientLevel level, int ticks, Camera camera)
	{
		return false;
	}
}
