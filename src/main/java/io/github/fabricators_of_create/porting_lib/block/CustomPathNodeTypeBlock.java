package io.github.fabricators_of_create.porting_lib.block;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

/**
 * Will be deprecated when https://github.com/FabricMC/fabric/pull/2437 is merged
 */
public interface CustomPathNodeTypeBlock {
	BlockPathTypes getBlockPathType(BlockState state, BlockGetter world, BlockPos pos, @Nullable Mob entity);
}
