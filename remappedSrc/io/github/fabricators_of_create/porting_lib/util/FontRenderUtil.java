package io.github.fabricators_of_create.porting_lib.util;

import io.github.fabricators_of_create.porting_lib.mixin.client.accessor.FontAccessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.FontStorage;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public final class FontRenderUtil {

	public static FontStorage getFontStorage(TextRenderer renderer, Identifier location) {
		return get(renderer).port_lib$getFontSet(location);
	}

	private static FontAccessor get(TextRenderer renderer) {
		return MixinHelper.cast(renderer);
	}

	private FontRenderUtil() {}
}
