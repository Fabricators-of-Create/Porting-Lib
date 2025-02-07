package io.github.fabricators_of_create.porting_lib.mixin.client;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.fabricators_of_create.porting_lib.model.CustomParticleIconModel;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;

@Mixin(InGameOverlayRenderer.class)
public abstract class ScreenEffectRendererMixin {
	@Unique
	@Nullable
	private static BlockPos viewBlockingPos = null;

	@ModifyReturnValue(method = "getViewBlockingState", at = @At("RETURN"))
	private static BlockState grabPos(BlockState state, @Local(ordinal = 0) Mutable pos) {
		viewBlockingPos = state == null ? null : pos.toImmutable();
		return state;
	}

	@WrapOperation(
			method = "renderScreenEffect",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/block/BlockModelShaper;getParticleIcon(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/client/renderer/texture/TextureAtlasSprite;"
			)
	)
	private static Sprite useCustomParticleSprite(BlockModels shaper, BlockState state, Operation<Sprite> original,
															  MinecraftClient mc, MatrixStack poseStack) {
		if (viewBlockingPos == null || !(mc.world instanceof RenderAttachedBlockView view))
			return original.call(shaper, state);
		BakedModel model = shaper.getModel(state);
		if (model instanceof CustomParticleIconModel custom) {
			Object data = view.getBlockEntityRenderAttachment(viewBlockingPos);
			return custom.getParticleIcon(data);
		}
		return original.call(shaper, state);
	}
}
