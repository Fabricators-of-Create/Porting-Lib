package io.github.fabricators_of_create.porting_lib.util;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

// Will be removed in 1.19.1
@Deprecated(forRemoval = true)
public class LevelUtil {
	public static boolean isAreaLoaded(LevelAccessor world, BlockPos center, int range) {
		return world.isAreaLoaded(center, range);
	}

	public static void markAndNotifyBlock(Level level, BlockPos pos, @Nullable LevelChunk levelchunk,
										  BlockState oldState, BlockState newState, int flags, int recursionLeft) {
		level.markAndNotifyBlock(pos, levelchunk, oldState, newState, flags, recursionLeft);
	}
}
