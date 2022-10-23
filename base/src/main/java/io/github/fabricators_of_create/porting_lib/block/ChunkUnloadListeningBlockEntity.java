package io.github.fabricators_of_create.porting_lib.block;

import io.github.fabricators_of_create.porting_lib.extensions.extensions.BlockEntityExtensions;

public interface ChunkUnloadListeningBlockEntity {
	default void onChunkUnloaded() {
		if (this instanceof BlockEntityExtensions ex) {
			ex.invalidateCaps();
		}
	}
}
