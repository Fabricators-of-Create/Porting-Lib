package io.github.fabricators_of_create.porting_lib.block;

import io.github.fabricators_of_create.porting_lib.extensions.BlockEntityExtensions;

@Deprecated
public interface ChunkUnloadListeningBlockEntity {
	default void onChunkUnloaded() {
		((BlockEntityExtensions)this).invalidateCaps();
	}
}
