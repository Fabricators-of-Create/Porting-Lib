package io.github.fabricators_of_create.porting_lib.blocks.mixin.common;

import io.github.fabricators_of_create.porting_lib.blocks.extensions.VanillaCustomExpBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.core.BlockPos;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(DropExperienceBlock.class)
public abstract class DropExperienceBlockMixin implements VanillaCustomExpBlock {
	@Shadow
	@Final
	private IntProvider xpRange;

	// Port Lib: Patch-in override for getExpDrop.
	@Override
	public int port_lib$getExpDrop(BlockState state, LevelAccessor level, BlockPos pos,
								   @Nullable BlockEntity blockEntity,
								   @Nullable Entity breaker, ItemStack tool) {
		return this.xpRange.sample(level.getRandom());
	}
}
