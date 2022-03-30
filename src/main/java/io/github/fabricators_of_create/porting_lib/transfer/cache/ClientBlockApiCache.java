package io.github.fabricators_of_create.porting_lib.transfer.cache;

import io.github.fabricators_of_create.porting_lib.extensions.ClientLevelExtensions;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientBlockEntityEvents;

public interface ClientBlockApiCache {
	void invalidate();

	static void init() {
		ClientBlockEntityEvents.BLOCK_ENTITY_LOAD.register((be, level) ->
				((ClientLevelExtensions) level).port_lib$invalidateCache(be.getBlockPos()));
		ClientBlockEntityEvents.BLOCK_ENTITY_UNLOAD.register((be, level) ->
				((ClientLevelExtensions) level).port_lib$invalidateCache(be.getBlockPos()));
	}
}
