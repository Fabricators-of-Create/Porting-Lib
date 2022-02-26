package io.github.fabricators_of_create.porting_lib.extensions;

import net.minecraft.core.BlockPos;

public interface AbstractMinecartExtensions {
	void port_lib$moveMinecartOnRail(BlockPos pos);

	BlockPos port_lib$getCurrentRailPos();

	default float port_lib$getMaxSpeedOnRail() {
		return 1.2f; // default in Forge
	}
}
