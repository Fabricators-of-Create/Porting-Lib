package io.github.fabricators_of_create.porting_lib.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mojang.blaze3d.pipeline.RenderTarget;

import net.minecraft.client.renderer.PostChain;

@Mixin(PostChain.class)
public abstract class PostChainMixin {
	@Shadow
	@Final
	private RenderTarget screenTarget;

	@Inject(
			method = "addTempTarget",
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;setClearColor(FFFF)V",
					shift = At.Shift.AFTER
			)
	)
	public void port_lib$isStencil(String name, int width, int height, CallbackInfo ci, @Local RenderTarget rendertarget) {
		if (screenTarget.isStencilEnabled()) {
			rendertarget.enableStencil();
		}
	}
}
