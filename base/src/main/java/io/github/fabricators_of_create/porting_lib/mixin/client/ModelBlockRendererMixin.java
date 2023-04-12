package io.github.fabricators_of_create.porting_lib.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import io.github.fabricators_of_create.porting_lib.block.LightEmissiveBlock;
import net.minecraft.client.renderer.block.ModelBlockRenderer;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ModelBlockRenderer.class, priority = 900)
public class ModelBlockRendererMixin {
	@ModifyExpressionValue(
			method = "tesselateBlock",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/state/BlockState;getLightEmission()I"
			)
	)
	private int port_lib$customLight(int emission, BlockAndTintGetter world, BakedModel model, BlockState state,
									  BlockPos pos, PoseStack matrix, VertexConsumer vertexConsumer, boolean cull,
									  RandomSource random, long seed, int overlay) {
		if (state.getBlock() instanceof LightEmissiveBlock custom) {
			return custom.getLightEmission(state, world, pos);
		}
		return emission;
	}
}
