package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.fabricators_of_create.porting_lib.block.CustomPathNodeTypeBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

@Mixin(LandPathNodeMaker.class)
public abstract class WalkNodeEvaluatorMixin {
	//TODO: This might not be correct
	@Inject(method = "getBlockPathTypeRaw", at = @At("HEAD"), cancellable = true)
	private static void port_lib$getCommonNodeType(BlockView iBlockReader, BlockPos blockPos, CallbackInfoReturnable<PathNodeType> cir) {
		Block block = iBlockReader.getBlockState(blockPos).getBlock();
		if (block instanceof CustomPathNodeTypeBlock customPathBlock) {
			PathNodeType pathType = customPathBlock.getAiPathNodeType(iBlockReader.getBlockState(blockPos), iBlockReader, blockPos, null);
			if (pathType != null)
				cir.setReturnValue(pathType);
		}
	}
}
