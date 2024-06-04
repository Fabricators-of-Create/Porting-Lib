package io.github.fabricators_of_create.porting_lib.fluids.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import io.github.fabricators_of_create.porting_lib.fluids.FluidInteractionRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(LiquidBlock.class)
public class LiquidBlockMixin {
	@ModifyExpressionValue(method = "onPlace", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/LiquidBlock;shouldSpreadLiquid(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
	private boolean canPlaceCustom(boolean original, BlockState blockState, Level level, BlockPos blockPos) {
		if (!FluidInteractionRegistry.canInteract(level, blockPos, false))
			return true;
		return original;
	}

	@ModifyExpressionValue(method = "neighborChanged", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/LiquidBlock;shouldSpreadLiquid(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
	private boolean neighborChangedCustom(boolean original, BlockState blockState, Level level, BlockPos blockPos) {
		if (!FluidInteractionRegistry.canInteract(level, blockPos, false))
			return true;
		return original;
	}
}
