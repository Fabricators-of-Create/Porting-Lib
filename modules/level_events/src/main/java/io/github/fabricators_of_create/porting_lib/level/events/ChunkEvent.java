package io.github.fabricators_of_create.porting_lib.level.events;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;

/**
 * ChunkEvent is fired when an event involving a chunk occurs.<br>
 * If a method utilizes this {@link BaseEvent} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * {@link #chunk} contains the Chunk this event is affecting.<br>
 * <br>
 **/
public abstract class ChunkEvent extends LevelEvent {
	private final ChunkAccess chunk;

	public ChunkEvent(ChunkAccess chunk) {
		super(chunk instanceof LevelChunk levelChunk ? levelChunk.getLevel() : null);
		this.chunk = chunk;
	}

	public ChunkEvent(ChunkAccess chunk, LevelAccessor level) {
		super(level);
		this.chunk = chunk;
	}

	public ChunkAccess getChunk() {
		return chunk;
	}
}
