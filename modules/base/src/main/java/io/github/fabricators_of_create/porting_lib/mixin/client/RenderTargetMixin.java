package io.github.fabricators_of_create.porting_lib.mixin.client;

import com.llamalad7.mixinextras.injector.WrapWithCondition;

import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import io.github.fabricators_of_create.porting_lib.extensions.extensions.RenderTargetExtensions;

import net.minecraft.client.Minecraft;

@Mixin(RenderTarget.class)
public abstract class RenderTargetMixin implements RenderTargetExtensions {
	@Shadow
	protected int depthBufferId;

	@Shadow
	public int viewWidth;

	@Shadow
	public int viewHeight;

	@Shadow
	public int width;

	@Shadow
	public int height;

	@Unique
	private boolean port_lib$stencilEnabled = false;

	@Shadow
	public abstract void resize(int width, int height, boolean clearError);

	@WrapWithCondition(
			method = "createBuffers",
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/blaze3d/platform/GlStateManager;_glFramebufferTexture2D(IIIII)V",
					ordinal = 1,
					remap = false
			)
	)
	private boolean port_lib$stencilBuffer(int i, int j, int k, int l, int m) {
		if (port_lib$stencilEnabled) {
			GlStateManager._glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, 3553, this.depthBufferId, 0);
			GlStateManager._glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_STENCIL_ATTACHMENT, 3553, this.depthBufferId, 0);
			return false;
		}
		return true;
	}

	// separate ModifyArg injects instead of one ModifyArgs to avoid creating objects

	@ModifyArg(method = "createBuffers",
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/blaze3d/platform/GlStateManager;_texImage2D(IIIIIIIILjava/nio/IntBuffer;)V",
					ordinal = 0,
					remap = false
			),
			index = 2
	)
	private int port_lib$modifyK(int original) {
		return port_lib$stencilEnabled ? GL30.GL_DEPTH32F_STENCIL8 : original;
	}

	@ModifyArg(method = "createBuffers",
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/blaze3d/platform/GlStateManager;_texImage2D(IIIIIIIILjava/nio/IntBuffer;)V",
					ordinal = 0,
					remap = false
			),
			index = 6
	)
	private int port_lib$modifyO(int original) {
		return port_lib$stencilEnabled ? GL30.GL_DEPTH_STENCIL : original;
	}

	@ModifyArg(method = "createBuffers",
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/blaze3d/platform/GlStateManager;_texImage2D(IIIIIIIILjava/nio/IntBuffer;)V",
					ordinal = 0,
					remap = false
			),
			index = 7
	)
	private int port_lib$modifyP(int original) {
		return port_lib$stencilEnabled ? GL30.GL_FLOAT_32_UNSIGNED_INT_24_8_REV : original;
	}

	@Unique
	@Override
	public void enableStencil() {
		if (port_lib$stencilEnabled) return;
		port_lib$stencilEnabled = true;
		this.resize(viewWidth, viewHeight, Minecraft.ON_OSX);
	}

	@Override
	public void disableStencil() {
		if (!port_lib$stencilEnabled) return;
		port_lib$stencilEnabled = false;
		this.resize(viewWidth, viewHeight, Minecraft.ON_OSX);
	}

	@Unique
	@Override
	public boolean isStencilEnabled() {
		return this.port_lib$stencilEnabled;
	}
}
