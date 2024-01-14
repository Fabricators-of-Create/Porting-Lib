package io.github.fabricators_of_create.porting_lib.util;

import io.github.fabricators_of_create.porting_lib.common.util.MixinHelper;
import io.github.fabricators_of_create.porting_lib.mixin.accessors.common.accessor.PlayerAccessor;

import net.minecraft.world.entity.player.Player;

public class PlayerEntityHelper {
	public static void closeScreen(Player player) {
		get(player).port_lib$closeScreen();
	}

	private static PlayerAccessor get(Player player) {
		return MixinHelper.cast(player);
	}

	private PlayerEntityHelper() {}
}
