package io.github.fabricators_of_create.porting_lib.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface MinecartPassHandlerBlock {
	void onMinecartPass(BlockState state, World world, BlockPos pos, AbstractMinecartEntity cart);
}
