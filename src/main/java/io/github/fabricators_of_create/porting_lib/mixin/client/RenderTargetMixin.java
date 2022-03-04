package io.github.fabricators_of_create.porting_lib.mixin.client;

import java.nio.IntBuffer;

import org.lwjgl.opengl.GL30;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import io.github.fabricators_of_create.porting_lib.extensions.RenderTargetExtensions;

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

	@Redirect(
			method = "createBuffers",
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/blaze3d/platform/GlStateManager;_glFramebufferTexture2D(IIIII)V",
					ordinal = 1,
					remap = false
			)
	)
	private void port_lib$stencilBuffer(int i, int j, int k, int l, int m) {
		if(!port_lib$stencilEnabled)
			GlStateManager._glFramebufferTexture2D(36160, 36096, 3553, this.depthBufferId, 0);
		else {
			GlStateManager._glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, 3553, this.depthBufferId, 0);
			GlStateManager._glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_STENCIL_ATTACHMENT, 3553, this.depthBufferId, 0);
		}
	}

	@Redirect(
			method = "createBuffers",
			at = @At(
					value = "INVOKE",
					target = "Lcom/mojang/blaze3d/platform/GlStateManager;_texImage2D(IIIIIIIILjava/nio/IntBuffer;)V",
					ordinal = 0,
					remap = false
			)
	)
	private void port_lib$stencilBuffer1(int i, int j, int k, int l, int m, int n, int o, int p, IntBuffer intBuffer) {
		if (!port_lib$stencilEnabled)
			GlStateManager._texImage2D(3553, 0, 6402, this.width, this.height, 0, 6402, 5126, null);
		else
			GlStateManager._texImage2D(3553, 0, GL30.GL_DEPTH32F_STENCIL8, this.width, this.height, 0, GL30.GL_DEPTH_STENCIL, GL30.GL_FLOAT_32_UNSIGNED_INT_24_8_REV, null);
	}

	@Unique
	@Override
	public void enableStencil() {
		if(port_lib$stencilEnabled) return;
		port_lib$stencilEnabled = true;
		this.resize(viewWidth, viewHeight, Minecraft.ON_OSX);
	}

	@Unique
	@Override
	public boolean isStencilEnabled() {
		return this.port_lib$stencilEnabled;
	}
}
