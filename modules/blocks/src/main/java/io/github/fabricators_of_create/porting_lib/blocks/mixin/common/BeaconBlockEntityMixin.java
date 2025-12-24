package io.github.fabricators_of_create.porting_lib.blocks.mixin.common;

import com.llamalad7.mixinextras.sugar.Share;

import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;

import net.minecraft.world.level.block.BeaconBeamBlock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.blocks.extensions.BeaconColorMultiplierBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BeaconBlockEntity.class)
public class BeaconBlockEntityMixin {
	@Definition(id = "block", local = @Local(type = Block.class))
	@Definition(id = "BeaconBeamBlock", type = BeaconBeamBlock.class)
	@Expression("block instanceof BeaconBeamBlock")
	@ModifyExpressionValue(method = "tick", at = @At("MIXINEXTRAS:EXPRESSION"))
	private static boolean isBeaconColorMultiplerBlock(boolean original, Level level, BlockPos pos, @Local Block block, @Local(ordinal = 1) BlockState blockState, @Local(ordinal = 1) BlockPos beaconPos, @Share("result") LocalIntRef colorMultiplier) {
		if (block instanceof BeaconColorMultiplierBlock colorBlock) {
			Integer result = colorBlock.getBeaconColorMultiplier(blockState, level, beaconPos, pos);
			if (result != null) {
				colorMultiplier.set(result);
				return false;
			}

			return true;
		}
		return original;
	}

	@WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/DyeColor;getTextureDiffuseColor()I"))
	private static int modifyBeaconColor(DyeColor instance, Operation<Integer> original, Level level, BlockPos pos, @Local(ordinal = 1) BlockPos beaconPos, @Share("result") LocalIntRef colorMultiplier) {
		BlockState blockState = level.getBlockState(beaconPos);
		if (blockState.getBlock() instanceof BeaconColorMultiplierBlock)
			return colorMultiplier.get();
		return original.call(instance);
	}
}
