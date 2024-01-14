package io.github.fabricators_of_create.porting_lib.chunk.loading.extensions;

import net.minecraft.server.level.TicketType;
import net.minecraft.world.level.ChunkPos;

public interface ServerChunkCacheExtension {
	default <T> void addRegionTicket(TicketType<T> pType, ChunkPos pPos, int pDistance, T pValue, boolean forceTicks) {
		throw new RuntimeException();
	}

	default <T> void removeRegionTicket(TicketType<T> pType, ChunkPos pPos, int pDistance, T pValue, boolean forceTicks) {
		throw new RuntimeException();
	}
}
