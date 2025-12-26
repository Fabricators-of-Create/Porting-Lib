package io.github.fabricators_of_create.porting_lib.level.events;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * Base class for events involving chunks.
 */
public abstract class ChunkEvent<T extends ChunkAccess> extends LevelEvent {
	private final T chunk;

	public ChunkEvent(T chunk) {
		super(chunk instanceof LevelChunk levelChunk ? levelChunk.getLevel() : null);
		this.chunk = chunk;
	}

	public ChunkEvent(T chunk, LevelAccessor level) {
		super(level);
		this.chunk = chunk;
	}

	public T getChunk() {
		return chunk;
	}
}
