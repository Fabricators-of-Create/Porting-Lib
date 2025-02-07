package io.github.fabricators_of_create.porting_lib.mixin.client;

import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.ShaderEffect;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ShaderEffect.class)
public abstract class PostChainMixin {
	@Shadow
	@Final
	private Framebuffer screenTarget;

	@Inject(
			method = "addTempTarget",
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;setClearColor(FFFF)V",
					shift = At.Shift.AFTER
			),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	public void port_lib$isStencil(String name, int width, int height, CallbackInfo ci, Framebuffer rendertarget) {
		if (screenTarget.isStencilEnabled()) {
			rendertarget.enableStencil();
		}
	}
}
