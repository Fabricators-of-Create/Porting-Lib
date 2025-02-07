package io.github.fabricators_of_create.porting_lib.event.common;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;

public interface ChunkTrackingCallback {

	Event<Watch> WATCH = EventFactory.createArrayBacked(Watch.class, callbacks -> (entity, chunkpos, level) -> {
		for (Watch e : callbacks)
			e.onChunkWatch(entity, chunkpos, level);
	});

	Event<Unwatch> UNWATCH = EventFactory.createArrayBacked(Unwatch.class, callbacks -> (entity, chunkpos, level) -> {
		for (Unwatch e : callbacks)
			e.onChunkUnwatch(entity, chunkpos, level);
	});

	@FunctionalInterface
	interface Watch {
		void onChunkWatch(ServerPlayerEntity entity, ChunkPos chunkpos, ServerWorld level);
	}

	@FunctionalInterface
	interface Unwatch {
		void onChunkUnwatch(ServerPlayerEntity entity, ChunkPos chunkpos, ServerWorld level);
	}
}
