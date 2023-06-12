package io.github.fabricators_of_create.porting_lib.entity.extensions;

import net.minecraft.core.BlockPos;

public interface AbstractMinecartExtensions {
	default void moveMinecartOnRail(BlockPos pos) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default BlockPos getCurrentRailPos() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default float getMaxSpeedOnRail() {
		return 1.2f; // default in Forge
	}
}
