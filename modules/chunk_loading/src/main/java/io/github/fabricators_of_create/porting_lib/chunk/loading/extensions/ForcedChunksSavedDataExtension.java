package io.github.fabricators_of_create.porting_lib.chunk.loading.extensions;

import io.github.fabricators_of_create.porting_lib.chunk.loading.ForcedChunkManager;

public interface ForcedChunksSavedDataExtension {
	default ForcedChunkManager.TicketTracker<net.minecraft.core.BlockPos> getBlockForcedChunks() {
		throw new RuntimeException();
	}

	default ForcedChunkManager.TicketTracker<java.util.UUID> getEntityForcedChunks() {
		throw new RuntimeException();
	}
}
