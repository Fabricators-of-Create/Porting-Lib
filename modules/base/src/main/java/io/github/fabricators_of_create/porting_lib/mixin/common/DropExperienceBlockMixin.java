package io.github.fabricators_of_create.porting_lib.mixin.common;

import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import io.github.fabricators_of_create.porting_lib.block.CustomExpBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(DropExperienceBlock.class)
public abstract class DropExperienceBlockMixin implements CustomExpBlock {
	@Shadow
	@Final
	private IntProvider xpRange;

	@Override
	public int getExpDrop(BlockState state, net.minecraft.world.level.LevelAccessor level, BlockPos pos,
						  @org.jetbrains.annotations.Nullable net.minecraft.world.level.block.entity.BlockEntity blockEntity,
						  @org.jetbrains.annotations.Nullable net.minecraft.world.entity.Entity breaker, ItemStack tool) {
		return this.xpRange.sample(level.getRandom());
	}
}
