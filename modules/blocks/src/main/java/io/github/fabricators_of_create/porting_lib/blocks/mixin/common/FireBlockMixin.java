package io.github.fabricators_of_create.porting_lib.blocks.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.blocks.extensions.CustomBurnabilityBlock;
import io.github.fabricators_of_create.porting_lib.blocks.extensions.FireSourceBlock;
import io.github.fabricators_of_create.porting_lib.blocks.extensions.FlammableBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.FireBlock;

import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireBlock.class)
public class FireBlockMixin {
	@WrapOperation(method = "checkBurnOut", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/FireBlock;getBurnOdds(Lnet/minecraft/world/level/block/state/BlockState;)I"))
	private int getBurnOdds(FireBlock instance, BlockState state, Operation<Integer> original, Level level, BlockPos pos) {
		// TODO: find some way in hell to pass the direction
		if (state.getBlock() instanceof FlammableBlock block) {
			return block.getFlammability(state, level, pos, null);
		}
		return original.call(instance, state);
	}

	@WrapOperation(method = "getIgniteOdds(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/FireBlock;getIgniteOdds(Lnet/minecraft/world/level/block/state/BlockState;)I"))
	private int getFireSpreadSpeed(FireBlock instance, BlockState state, Operation<Integer> original, LevelReader level, BlockPos pos, @Local(ordinal = 0) int chance, @Local Direction direction) {
		if (state.getBlock() instanceof FlammableBlock block) {
			return block.getFireSpreadSpeed(state, level, pos.relative(direction), direction.getOpposite());
		}
		return original.call(instance, state);
	}

	@Inject(
			method = "checkBurnOut",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;"),
			cancellable = true
	)
	private void port_lib$onCaughtFire(Level level, BlockPos pos, int spreadFactor, RandomSource random, int currentAge, CallbackInfo ci, @Local BlockState blockState) {
		if (blockState.getBlock() instanceof FlammableBlock fireBlock) {
			fireBlock.onCaughtFire(blockState, level, pos, null, null);
			ci.cancel();
		}
	}

	@ModifyExpressionValue(method = "canBurn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/FireBlock;getIgniteOdds(Lnet/minecraft/world/level/block/state/BlockState;)I"))
	private int port_lib$customBurnability(int igniteOdds, BlockState state) {
		if (state.getBlock() instanceof CustomBurnabilityBlock custom) {
			boolean burnable = custom.canBurn(state);
			// replace igniteOdds. normally, burnable if igniteOdds > 0
			return burnable ? 1 : 0;
		}
		return igniteOdds;
	}

	@ModifyVariable(method = "tick", at = @At("STORE"), index = 6)
	private boolean customFireSource(boolean value, BlockState otherState, ServerLevel world, BlockPos pos, @Local(index = 5) BlockState state) {
		if (state.getBlock() instanceof FireSourceBlock fireSource)
			return fireSource.isFireSource(state, world, pos, Direction.UP);
		return value;
	}
}
