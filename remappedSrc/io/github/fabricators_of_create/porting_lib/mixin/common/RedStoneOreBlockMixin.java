package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.block.CustomExpBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.RedstoneOreBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RedstoneOreBlock.class)
public abstract class RedStoneOreBlockMixin implements CustomExpBlock {
	@Override
	public int getExpDrop(BlockState state, net.minecraft.world.WorldView world, BlockPos pos, int fortune, int silktouch) {
		return silktouch == 0 ? 1 + ((World)world).getRandom().nextInt(5) : 0;
	}
}
