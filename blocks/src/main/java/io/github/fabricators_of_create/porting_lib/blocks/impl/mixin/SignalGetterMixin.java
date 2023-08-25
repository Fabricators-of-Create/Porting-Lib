package io.github.fabricators_of_create.porting_lib.blocks.impl.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.fabricators_of_create.porting_lib.blocks.api.addons.WeakPowerCheckingBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SignalGetter.class)
public interface SignalGetterMixin extends BlockGetter {
	@WrapOperation(
			method = "getSignal",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/state/BlockState;isRedstoneConductor(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Z"
			)
	)
	private boolean port_lib$modifyRedstoneSignal(BlockState state, BlockGetter level, BlockPos pos, Operation<Boolean> original,
												  BlockPos pos2, Direction facing) {
		if (state.getBlock() instanceof WeakPowerCheckingBlock checking) {
			return checking.shouldCheckWeakPower(state, (SignalGetter) this, pos, facing);
		}
		return original.call(state, level, pos);
	}
}
