package io.github.fabricators_of_create.porting_lib.util;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.fabricators_of_create.porting_lib.mixin.client.accessor.KeyMappingAccessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;

@Environment(EnvType.CLIENT)
public final class KeyBindingHelper {
	public static InputConstants.Key getKeyCode(KeyMapping keyBinding) {
		return get(keyBinding).port_lib$getKey();
	}

	private static KeyMappingAccessor get(KeyMapping keyBinding) {
		return MixinHelper.cast(keyBinding);
	}

	private KeyBindingHelper() { }
}
