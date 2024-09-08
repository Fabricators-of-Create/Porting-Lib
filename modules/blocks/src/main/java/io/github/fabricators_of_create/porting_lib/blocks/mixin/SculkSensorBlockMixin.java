package io.github.fabricators_of_create.porting_lib.blocks.mixin;

import io.github.fabricators_of_create.porting_lib.blocks.extensions.VanillaCustomExpBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SculkSensorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SculkSensorBlock.class)
public class SculkSensorBlockMixin implements VanillaCustomExpBlock {
	// Port Lib: Patch-in override for getExpDrop.
	@Override
	public int port_lib$getExpDrop(BlockState state, LevelAccessor level, BlockPos pos,
								   @Nullable BlockEntity blockEntity,
								   @Nullable Entity breaker, ItemStack tool) {
		return 5;
	}
}
