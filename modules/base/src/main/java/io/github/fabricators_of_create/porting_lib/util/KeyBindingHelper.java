package io.github.fabricators_of_create.porting_lib.util;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;

import io.github.fabricators_of_create.porting_lib.common.util.MixinHelper;
import io.github.fabricators_of_create.porting_lib.mixin.accessors.client.accessor.KeyMappingAccessor;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;

@Environment(EnvType.CLIENT)
public final class KeyBindingHelper {
	public static boolean isActiveAndMatches(KeyMapping keyMapping, Key keyCode) {
		return keyCode != InputConstants.UNKNOWN && keyCode.equals(net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.getBoundKeyOf(keyMapping));
	}

	private static KeyMappingAccessor get(KeyMapping keyBinding) {
		return MixinHelper.cast(keyBinding);
	}

	private KeyBindingHelper() { }
}
