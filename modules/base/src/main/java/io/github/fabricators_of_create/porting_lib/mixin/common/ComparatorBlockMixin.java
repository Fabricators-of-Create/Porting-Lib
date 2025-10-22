package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.extensions.ComparatorBlockExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComparatorBlock;

import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(ComparatorBlock.class)
public class ComparatorBlockMixin implements ComparatorBlockExtension {
	@Override
	public void onNeighborChange(BlockState state, LevelReader world, BlockPos pos, BlockPos neighbor) {
		if (pos.getY() == neighbor.getY() && world instanceof Level && !world.isClientSide()) {
			state.neighborChanged((Level)world, pos, world.getBlockState(neighbor).getBlock(), neighbor, false);
		}
	}

	@Override
	public boolean getWeakChanges(BlockState state, LevelReader level, BlockPos pos) {
		return state.is(Blocks.COMPARATOR);
	}
}
