package io.github.fabricators_of_create.porting_lib.models.mixin.client;

import javax.annotation.Nullable;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.mojang.blaze3d.vertex.PoseStack;

import io.github.fabricators_of_create.porting_lib.models.CustomParticleIconModel;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(ScreenEffectRenderer.class)
public abstract class ScreenEffectRendererMixin {
	@Unique
	@Nullable
	private static BlockPos viewBlockingPos = null;

	@ModifyReturnValue(method = "getViewBlockingState", at = @At("RETURN"))
	private static BlockState grabPos(BlockState state, @Local(ordinal = 0) MutableBlockPos pos) {
		viewBlockingPos = state == null ? null : pos.immutable();
		return state;
	}

	@WrapOperation(
			method = "renderScreenEffect",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/block/BlockModelShaper;getParticleIcon(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;"
			)
	)
	private static TextureAtlasSprite useCustomParticleSprite(BlockModelShaper shaper, BlockState state, Operation<TextureAtlasSprite> original,
															  Minecraft mc, PoseStack poseStack) {
		if (viewBlockingPos == null || !(mc.level instanceof RenderAttachedBlockView view))
			return original.call(shaper, state);
		BakedModel model = shaper.getBlockModel(state);
		if (model instanceof CustomParticleIconModel custom) {
			Object data = view.getBlockEntityRenderAttachment(viewBlockingPos);
			return custom.getParticleIcon(data);
		}
		return original.call(shaper, state);
	}
}
