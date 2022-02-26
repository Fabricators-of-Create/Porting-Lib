package io.github.fabricators_of_create.porting_lib.mixin.common;

import java.util.Iterator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import io.github.fabricators_of_create.porting_lib.block.NeighborChangeListeningBlock;
import io.github.fabricators_of_create.porting_lib.block.WeakPowerCheckingBlock;
import io.github.fabricators_of_create.porting_lib.util.MixinHelper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(Level.class)
public abstract class LevelMixin implements LevelAccessor {
	@Shadow
	public abstract BlockState getBlockState(BlockPos blockPos);

	@Inject(method = "getSignal", at = @At("RETURN"), cancellable = true)
	public void port_lib$getRedstoneSignal(BlockPos blockPos, Direction direction, CallbackInfoReturnable<Integer> cir) {
		BlockState port_lib$blockstate = MixinHelper.<Level>cast(this).getBlockState(blockPos);
		int port_lib$i = port_lib$blockstate.getSignal(MixinHelper.<Level>cast(this), blockPos, direction);

		if (port_lib$blockstate.getBlock() instanceof WeakPowerCheckingBlock) {
			cir.setReturnValue(
					((WeakPowerCheckingBlock) port_lib$blockstate.getBlock()).shouldCheckWeakPower(port_lib$blockstate, MixinHelper.<Level>cast(this), blockPos, direction)
							? Math.max(port_lib$i, MixinHelper.<Level>cast(this).getDirectSignalTo(blockPos))
							: port_lib$i);
		}
	}

	@Inject(
			method = "updateNeighbourForOutputSignal",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;",
					shift = At.Shift.AFTER
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	public void port_lib$updateNeighbourForOutputSignal(BlockPos pos, Block block, CallbackInfo ci,
												   Iterator<?> var3, Direction direction, BlockPos blockPos2) {
		if (block instanceof NeighborChangeListeningBlock listener) {
			listener.onNeighborChange(getBlockState(blockPos2), this, blockPos2, pos);
		}
	}
}
