package io.github.fabricators_of_create.porting_lib.extensions.extensions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;

public interface LevelReaderExtensions {
	default boolean isAreaLoaded(BlockPos center, int range) {
		return ((LevelReader)this).hasChunksAt(center.offset(-range, -range, -range), center.offset(range, range, range));
	}
}
