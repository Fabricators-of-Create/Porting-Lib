package io.github.fabricators_of_create.porting_lib.common.util;

import java.util.Locale;

import io.github.fabricators_of_create.porting_lib.common.mixin.client.accessor.MinecraftAccessor;

import io.github.fabricators_of_create.porting_lib.core.util.MixinHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.LanguageManager;

@Environment(EnvType.CLIENT)
public final class MinecraftClientUtil {
	public static float getRenderPartialTicksPaused(Minecraft minecraft) {
		return get(minecraft).port_lib$pausePartialTick();
	}

	public static Locale getLocale() {
		LanguageManager manager = Minecraft.getInstance().getLanguageManager();
		return manager.getJavaLocale(manager.getSelected());
	}

	private static MinecraftAccessor get(Minecraft minecraft) {
		return MixinHelper.cast(minecraft);
	}

	private MinecraftClientUtil() {}
}
