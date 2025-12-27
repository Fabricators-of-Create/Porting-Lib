package io.github.fabricators_of_create.porting_lib.transfer.client.internal.injects;

import io.github.fabricators_of_create.porting_lib.transfer.client.internal.cache.ClientBlockApiCache;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.core.BlockPos;

@Internal
public interface ClientLevelInjection {
	default void port_lib$registerCache(BlockPos pos, ClientBlockApiCache cache) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}

	default void port_lib$invalidateCache(BlockPos pos) {
		throw new RuntimeException("this should be overridden via mixin. what?");
	}
}
