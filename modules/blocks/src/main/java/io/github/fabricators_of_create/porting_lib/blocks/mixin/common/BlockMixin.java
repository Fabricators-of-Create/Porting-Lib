package io.github.fabricators_of_create.porting_lib.blocks.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Block.class)
public abstract class BlockMixin extends BlockBehaviour {
	private BlockMixin(BlockBehaviour.Properties properties) {
		super(properties);
	}

	@ModifyExpressionValue(
			method = "shouldRenderFace",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/state/BlockState;skipRendering(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Z"
			)
	)
	private static boolean customFaceHiding(boolean orignial, BlockState pState, BlockGetter pLevel, BlockPos pOffset, Direction pFace, BlockPos pPos) {
		return orignial || (pState.port_lib$supportsExternalFaceHiding() && pLevel.getBlockState(pPos).port_lib$hidesNeighborFace(pLevel, pPos, pState, pFace.getOpposite()));
	}
}
