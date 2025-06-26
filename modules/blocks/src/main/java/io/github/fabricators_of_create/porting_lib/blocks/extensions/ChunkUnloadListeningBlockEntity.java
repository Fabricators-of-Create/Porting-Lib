package io.github.fabricators_of_create.porting_lib.blocks.extensions;

public interface ChunkUnloadListeningBlockEntity {
	default void onChunkUnloaded() {}
}
