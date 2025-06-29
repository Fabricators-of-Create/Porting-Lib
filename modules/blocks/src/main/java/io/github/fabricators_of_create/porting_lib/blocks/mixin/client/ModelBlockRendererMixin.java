package io.github.fabricators_of_create.porting_lib.blocks.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.fabricators_of_create.porting_lib.blocks.extensions.LightEmissiveBlock;
import net.minecraft.client.renderer.block.ModelBlockRenderer;

import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(value = ModelBlockRenderer.class, priority = 900)
public class ModelBlockRendererMixin {
	@WrapOperation(
			method = "tesselateBlock",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/state/BlockState;getLightEmission()I"
			)
	)
	private int customLight(BlockState instance, Operation<Integer> original, BlockAndTintGetter level, List<BlockModelPart> parts, BlockState state, BlockPos pos) {
		if (instance.getBlock() instanceof LightEmissiveBlock custom) {
			return custom.getLightEmission(instance, level, pos);
		}
		return original.call(instance);
	}
}
