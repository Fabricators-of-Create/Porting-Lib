package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.block.CustomExpBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.RedStoneOreBlock;

import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(RedStoneOreBlock.class)
public abstract class RedStoneOreBlockMixin implements CustomExpBlock {
	@Override
	public int getExpDrop(BlockState state, net.minecraft.world.level.LevelAccessor level, BlockPos pos,
						  @org.jetbrains.annotations.Nullable net.minecraft.world.level.block.entity.BlockEntity blockEntity,
						  @org.jetbrains.annotations.Nullable net.minecraft.world.entity.Entity breaker, ItemStack tool) {
		return UniformInt.of(1, 5).sample(level.getRandom());
	}
}
