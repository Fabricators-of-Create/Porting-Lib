package io.github.fabricators_of_create.porting_lib.item;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface BlockUseBypassingItem {
	boolean shouldBypass(BlockState state, BlockPos pos, World level, PlayerEntity player, Hand hand);
}
