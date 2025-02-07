package io.github.fabricators_of_create.porting_lib.util;

import io.github.fabricators_of_create.porting_lib.mixin.common.accessor.PlayerAccessor;
import net.minecraft.entity.player.PlayerEntity;

public class PlayerEntityHelper {
	public static void closeScreen(PlayerEntity player) {
		get(player).port_lib$closeScreen();
	}

	private static PlayerAccessor get(PlayerEntity player) {
		return MixinHelper.cast(player);
	}

	private PlayerEntityHelper() {}
}
