package io.github.fabricators_of_create.porting_lib.blocks.mixin.client;

import io.github.fabricators_of_create.porting_lib.blocks.extensions.CustomDisplayFluidOverlayBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.level.material.FluidState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;

@Mixin(LiquidBlockRenderer.class)
public abstract class LiquidBlockRendererMixin {
	@Definition(id = "block", local = @Local(type = Block.class))
	@Definition(id = "HalfTransparentBlock", type = HalfTransparentBlock.class)
	@Expression("block instanceof HalfTransparentBlock")
	@ModifyExpressionValue(method = "tesselate", at = @At("MIXINEXTRAS:EXPRESSION"))
	private boolean port_lib$checkShouldDisplayFluidOverlay(boolean original, @Local Block block, @Local(argsOnly = true) BlockAndTintGetter level, @Local(ordinal = 1) BlockPos pos, @Local(argsOnly = true) FluidState fluidState) {
		if (block instanceof CustomDisplayFluidOverlayBlock displayFluidOverlayBlock) {
			return displayFluidOverlayBlock.shouldDisplayFluidOverlay(level.getBlockState(pos), level, pos, fluidState);
		}

		return original;
	}
}
