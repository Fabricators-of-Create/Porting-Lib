package io.github.fabricators_of_create.porting_lib.extensions.extensions;

import net.minecraft.world.level.block.state.BlockState;

public interface CameraExtensions {
	default BlockState getBlockAtCamera() {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
