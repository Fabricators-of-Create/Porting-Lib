package io.github.fabricators_of_create.porting_lib.extensions.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import io.github.fabricators_of_create.porting_lib.extensions.extensions.BlockExtensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Block.class)
public abstract class BlockMixin extends BlockBehaviour implements BlockExtensions {
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
		return orignial || (pState.supportsExternalFaceHiding() && pLevel.getBlockState(pPos).hidesNeighborFace(pLevel, pPos, pState, pFace.getOpposite()));
	}
}
