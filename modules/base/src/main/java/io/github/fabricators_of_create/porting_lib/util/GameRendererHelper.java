package io.github.fabricators_of_create.porting_lib.util;

import io.github.fabricators_of_create.porting_lib.common.util.MixinHelper;
import io.github.fabricators_of_create.porting_lib.mixin.accessors.client.accessor.GameRendererAccessor;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;

public final class GameRendererHelper {
	public static double getFOVModifier(GameRenderer renderer, Camera camera, float tickDelta, boolean changingFov) {
		return get(renderer).port_lib$getFOVModifier(camera, tickDelta, changingFov);
	}

	private static GameRendererAccessor get(GameRenderer renderer) {
		return MixinHelper.cast(renderer);
	}

	private GameRendererHelper() {}
}
