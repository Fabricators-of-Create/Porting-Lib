package io.github.fabricators_of_create.porting_lib.blocks.injects;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.minecraft.world.level.block.state.BlockState;

public interface CameraInjection {
	default BlockState port_lib$getBlockAtCamera() {
		throw PortingLib.createMixinException("CameraInjection.port_lib$getBlockAtCamera()");
	}
}
