package io.github.fabricators_of_create.porting_lib.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

/**
 * Will be deprecated when https://github.com/FabricMC/fabric/pull/2437 is merged
 */
public interface CustomPathNodeTypeBlock {
	PathNodeType getAiPathNodeType(BlockState state, BlockView world, BlockPos pos, @Nullable MobEntity entity);
}
