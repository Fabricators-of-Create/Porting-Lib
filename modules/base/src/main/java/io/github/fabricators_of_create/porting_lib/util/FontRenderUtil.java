package io.github.fabricators_of_create.porting_lib.util;

import io.github.fabricators_of_create.porting_lib.common.util.MixinHelper;
import io.github.fabricators_of_create.porting_lib.mixin.accessors.client.accessor.FontAccessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public final class FontRenderUtil {

	public static FontSet getFontStorage(Font renderer, ResourceLocation location) {
		return get(renderer).port_lib$getFontSet(location);
	}

	private static FontAccessor get(Font renderer) {
		return MixinHelper.cast(renderer);
	}

	private FontRenderUtil() {}
}
