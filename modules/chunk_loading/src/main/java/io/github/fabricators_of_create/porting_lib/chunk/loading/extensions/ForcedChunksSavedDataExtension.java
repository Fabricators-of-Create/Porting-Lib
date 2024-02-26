package io.github.fabricators_of_create.porting_lib.chunk.loading.extensions;

import io.github.fabricators_of_create.porting_lib.chunk.loading.PortingLibChunkManager;

public interface ForcedChunksSavedDataExtension {
	default PortingLibChunkManager.TicketTracker<net.minecraft.core.BlockPos> getBlockForcedChunks() {
		throw new RuntimeException();
	}

	default PortingLibChunkManager.TicketTracker<java.util.UUID> getEntityForcedChunks() {
		throw new RuntimeException();
	}
}
