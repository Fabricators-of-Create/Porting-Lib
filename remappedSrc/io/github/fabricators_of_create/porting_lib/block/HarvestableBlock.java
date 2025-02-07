package io.github.fabricators_of_create.porting_lib.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.MiningToolItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public interface HarvestableBlock {
	boolean canHarvestBlock(BlockState state, BlockView world, BlockPos pos, PlayerEntity player);

	boolean isToolEffective(BlockState state, MiningToolItem tool);
}
