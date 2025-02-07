package io.github.fabricators_of_create.porting_lib.util;

import io.github.fabricators_of_create.porting_lib.mixin.client.accessor.KeyMappingAccessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.InputUtil.Key;

@Environment(EnvType.CLIENT)
public final class KeyBindingHelper {
	public static boolean isActiveAndMatches(KeyBinding keyMapping, Key keyCode) {
		return keyCode != InputUtil.UNKNOWN_KEY && keyCode.equals(getKeyCode(keyMapping));
	}

	public static InputUtil.Key getKeyCode(KeyBinding keyBinding) {
		return get(keyBinding).port_lib$getKey();
	}

	private static KeyMappingAccessor get(KeyBinding keyBinding) {
		return MixinHelper.cast(keyBinding);
	}

	private KeyBindingHelper() { }
}
