package io.github.fabricators_of_create.porting_lib.blocks.extensions;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

public interface OnLoadBlockEntity {
	/**
	 * Called when this is first added to the world (by {@link LevelChunk#addAndRegisterBlockEntity(BlockEntity)})
	 * or right before the first tick when the chunk is generated or loaded from disk.
	 * Override instead of adding {@code if (firstTick)} stuff in update.
	 */
	default void onLoad() {
	}
}
