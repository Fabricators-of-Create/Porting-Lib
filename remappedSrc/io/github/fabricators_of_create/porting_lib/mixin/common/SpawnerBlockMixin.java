package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.block.CustomExpBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SpawnerBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SpawnerBlock.class)
public abstract class SpawnerBlockMixin implements CustomExpBlock {

	@Override
	public int getExpDrop(BlockState state, net.minecraft.world.WorldView world, BlockPos pos, int fortune, int silktouch) {
		return 15 + ((World)world).getRandom().nextInt(15) + ((World)world).getRandom().nextInt(15);
	}
}
