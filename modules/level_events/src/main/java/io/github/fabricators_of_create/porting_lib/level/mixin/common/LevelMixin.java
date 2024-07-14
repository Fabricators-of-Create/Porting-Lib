package io.github.fabricators_of_create.porting_lib.level.mixin.common;

import io.github.fabricators_of_create.porting_lib.level.events.BlockEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

import net.minecraft.world.level.block.Block;

import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumSet;

@Mixin(Level.class)
public abstract class LevelMixin {
	@Shadow
	public abstract BlockState getBlockState(BlockPos blockPos);

	@Inject(method = "updateNeighborsAt", at = @At("HEAD"))
	private void neighborNotify(BlockPos pPos, Block block, CallbackInfo ci) {
		BlockEvent.NeighborNotifyEvent event = new BlockEvent.NeighborNotifyEvent((Level) (Object) this, pPos, getBlockState(pPos), EnumSet.allOf(Direction.class), false);
		event.sendEvent();
	}
}
