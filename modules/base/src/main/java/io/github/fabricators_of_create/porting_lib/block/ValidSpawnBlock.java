package io.github.fabricators_of_create.porting_lib.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

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
	default boolean isValidSpawn(BlockState state, BlockGetter level, BlockPos pos, SpawnPlacementType type, EntityType<?> entityType) {
		return state.isValidSpawn(level, pos, entityType);
	}
}
