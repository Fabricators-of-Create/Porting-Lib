package io.github.fabricators_of_create.porting_lib.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public interface CustomSoundTypeBlock {
	BlockSoundGroup getSoundType(BlockState state, WorldView world, BlockPos pos, Entity entity);
}
