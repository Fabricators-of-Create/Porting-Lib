package io.github.fabricators_of_create.porting_lib.extensions.extensions;

import io.github.fabricators_of_create.porting_lib.core.PortingLib;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

public interface BlockEntityExtensions {
	/**
	 * Gets a {@link CompoundTag} that can be used to store custom data for this block entity.
	 * It will be written, and read from disc, so it persists over world saves.
	 *
	 * @return A compound tag for custom persistent data
	 */
	default CompoundTag getPersistentData() {
		throw PortingLib.createMixinException("getPersistentData()");
	}

	/**
	 * Called when this is first added to the world (by {@link LevelChunk#addAndRegisterBlockEntity(BlockEntity)})
	 * or right before the first tick when the chunk is generated or loaded from disk.
	 * Override instead of adding {@code if (firstTick)} stuff in update.
	 */
	default void onLoad() {
	}
}
