package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.event.common.BlockEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DiodeBlock;

import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DiodeBlock.class)
public class DiodeBlockMixin {
	@Inject(method = "updateNeighborsInFront", at = @At("HEAD"), cancellable = true)
	private void notifyNeighborsEvent(Level pLevel, BlockPos pPos, BlockState blockState, CallbackInfo ci) {
		Direction direction = blockState.getValue(HorizontalDirectionalBlock.FACING);
		BlockEvents.NeighborNotifyEvent event = new BlockEvents.NeighborNotifyEvent(pLevel, pPos, pLevel.getBlockState(pPos), java.util.EnumSet.of(direction.getOpposite()), false);
		event.sendEvent();
		if (event.isCanceled())
			ci.cancel();
	}
}
