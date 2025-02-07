package io.github.fabricators_of_create.porting_lib.util;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public interface ValidSpawnBlock {
	/**
	 * Determines if a specified mob type can spawn on this block, returning false will
	 * prevent any mob from spawning on the block.
	 *
	 * @param state The current state
	 * @param level The current level
	 * @param pos Block position in level
	 * @param type The Mob Category Type
	 * @return True to allow a mob of the specified category to spawn, false to prevent it.
	 */
	default boolean isValidSpawn(BlockState state, BlockView level, BlockPos pos, SpawnRestriction.Location type, EntityType<?> entityType) {
		return state.allowsSpawning(level, pos, entityType);
	}
}
