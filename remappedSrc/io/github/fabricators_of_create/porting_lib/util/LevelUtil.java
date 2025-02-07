package io.github.fabricators_of_create.porting_lib.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ChunkHolder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;

public class LevelUtil {
	public static boolean isAreaLoaded(WorldAccess world, BlockPos center, int range) {
		if (range == 0) return world.isChunkLoaded(center);
		return world.isRegionLoaded(center.add(-range, -range, -range), center.add(range, range, range));
	}

	public static void markAndNotifyBlock(World level, BlockPos pos, @Nullable WorldChunk levelchunk,
										  BlockState oldState, BlockState newState, int flags, int recursionLeft) {
		Block block = newState.getBlock();
		BlockState oldState1 = level.getBlockState(pos);
		if (oldState1 == newState) {
			if (oldState != oldState1) {
				level.scheduleBlockRerenderIfNeeded(pos, oldState, oldState1);
			}

			if ((flags & 2) != 0 && (!level.isClient || (flags & 4) == 0) && (level.isClient || levelchunk.getLevelType() != null && levelchunk.getLevelType().isAfter(ChunkHolder.LevelType.TICKING))) {
				level.updateListeners(pos, oldState, newState, flags);
			}

			if ((flags & 1) != 0) {
				level.updateNeighbors(pos, oldState.getBlock());
				if (!level.isClient && newState.hasComparatorOutput()) {
					level.updateComparators(pos, block);
				}
			}

			if ((flags & 16) == 0 && recursionLeft > 0) {
				int i = flags & -34;
				oldState.prepare(level, pos, i, recursionLeft - 1);
				newState.updateNeighbors(level, pos, i, recursionLeft - 1);
				newState.prepare(level, pos, i, recursionLeft - 1);
			}

			level.onBlockChanged(pos, oldState, oldState1);
		}
	}
}
