package io.github.fabricators_of_create.porting_lib.extensions;

import io.github.fabricators_of_create.porting_lib.transfer.cache.ClientBlockApiCache;
import net.minecraft.core.BlockPos;

public interface ClientLevelExtensions {
	default void port_lib$registerCache(BlockPos pos, ClientBlockApiCache cache) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default void port_lib$invalidateCache(BlockPos pos) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
