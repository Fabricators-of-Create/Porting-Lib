package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.block.CustomExpBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.RedStoneOreBlock;

import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(RedStoneOreBlock.class)
public abstract class RedStoneOreBlockMixin implements CustomExpBlock {
	@Override
	public int getExpDrop(BlockState state, net.minecraft.world.level.LevelReader world, RandomSource randomSource, BlockPos pos, int fortune, int silktouch) {
		return silktouch == 0 ? 1 + randomSource.nextInt(5) : 0;
	}
}
