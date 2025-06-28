package io.github.fabricators_of_create.porting_lib.chunk.loading.extensions;

import io.github.fabricators_of_create.porting_lib.chunk.loading.ForcedChunkManager;
import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

import java.util.UUID;

public interface TicketStorageExtension {
	default ForcedChunkManager.TicketTracker<BlockPos> port_lib$getBlockForcedChunks() {
		throw PortingLib.createMixinException("TicketStorageExtension.port_lib$getBlockForcedChunks()");
	}

	default ForcedChunkManager.TicketTracker<UUID> port_lib$getEntityForcedChunks() {
		throw PortingLib.createMixinException("TicketStorageExtension.port_lib$getEntityForcedChunks()");
	}

	default boolean port_lib$shouldForceNaturalSpawning(ChunkPos chunkPos) {
		throw PortingLib.createMixinException("TicketStorageExtension.shouldForceNaturalSpawning()");
	}
}
