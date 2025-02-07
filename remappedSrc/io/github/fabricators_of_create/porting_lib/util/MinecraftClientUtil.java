package io.github.fabricators_of_create.porting_lib.util;

import java.util.Locale;

import io.github.fabricators_of_create.porting_lib.extensions.LanguageInfoExtensions;
import io.github.fabricators_of_create.porting_lib.mixin.client.accessor.MinecraftAccessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public final class MinecraftClientUtil {
	public static float getRenderPartialTicksPaused(MinecraftClient minecraft) {
		return get(minecraft).port_lib$pausePartialTick();
	}

	public static Locale getLocale() {
		return MinecraftClient.getInstance().getLanguageManager().getLanguage().getJavaLocale();
	}

	private static MinecraftAccessor get(MinecraftClient minecraft) {
		return MixinHelper.cast(minecraft);
	}

	private MinecraftClientUtil() {}
}
