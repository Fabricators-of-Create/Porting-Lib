package io.github.fabricators_of_create.porting_lib.util;

import java.util.Locale;

import io.github.fabricators_of_create.porting_lib.extensions.LanguageInfoExtensions;
import io.github.fabricators_of_create.porting_lib.mixin.client.accessor.MinecraftAccessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

@Environment(EnvType.CLIENT)
public final class MinecraftClientUtil {
	public static float getRenderPartialTicksPaused(Minecraft minecraft) {
		return get(minecraft).port_lib$pausePartialTick();
	}

	public static Locale getLocale() {
		return Minecraft.getInstance().getLanguageManager().getSelected().getJavaLocale();
	}

	private static MinecraftAccessor get(Minecraft minecraft) {
		return MixinHelper.cast(minecraft);
	}

	private MinecraftClientUtil() {}
}
