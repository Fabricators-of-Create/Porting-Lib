package io.github.fabricators_of_create.porting_lib.extensions;

import io.github.fabricators_of_create.porting_lib.transfer.cache.ClientBlockApiCache;
import net.minecraft.core.BlockPos;

public interface ClientLevelExtensions {
	void port_lib$registerCache(BlockPos pos, ClientBlockApiCache cache);

	void port_lib$invalidateCache(BlockPos pos);
}
