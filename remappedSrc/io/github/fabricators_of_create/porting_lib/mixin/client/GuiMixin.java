package io.github.fabricators_of_create.porting_lib.mixin.client;

import static net.minecraft.client.gui.DrawableHelper.GUI_ICONS_TEXTURE;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.fabricators_of_create.porting_lib.event.client.OverlayRenderCallback;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class GuiMixin {
	@Shadow
	@Final
	private MinecraftClient minecraft;
	@Unique
	public float port_lib$partialTicks;

	@Inject(method = "render", at = @At("HEAD"))
	public void port_lib$render(MatrixStack matrixStack, float f, CallbackInfo ci) {
		port_lib$partialTicks = f;
	}

	//This might be the wrong method to inject to
	@Inject(
			method = "renderPlayerHealth",
			at = @At(
					value = "INVOKE",
					shift = At.Shift.AFTER,
					target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V"
			),
			cancellable = true)
	private void port_lib$renderStatusBars(MatrixStack matrixStack, CallbackInfo ci) {
		if (OverlayRenderCallback.EVENT.invoker().onOverlayRender(matrixStack, port_lib$partialTicks, minecraft.getWindow(), OverlayRenderCallback.Types.AIR)) {
			ci.cancel();
		}
	}

	@Inject(
			method = "renderHearts",
			at = @At(
					value = "HEAD"
			),
			cancellable = true
	)
	private void port_lib$renderHealth(MatrixStack matrixStack, PlayerEntity player, int i, int j, int k, int l, float f, int m, int n, int o, boolean bl, CallbackInfo ci) {
		if (OverlayRenderCallback.EVENT.invoker().onOverlayRender(matrixStack, port_lib$partialTicks, minecraft.getWindow(), OverlayRenderCallback.Types.PLAYER_HEALTH)) {
			ci.cancel();
		}
	}

	@Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
	private void port_lib$renderCrosshair(MatrixStack matrixStack, CallbackInfo ci) {
		if (OverlayRenderCallback.EVENT.invoker().onOverlayRender(matrixStack, port_lib$partialTicks, minecraft.getWindow(), OverlayRenderCallback.Types.CROSSHAIRS)) {
			ci.cancel();
			return;
		}
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, GUI_ICONS_TEXTURE);
		RenderSystem.enableBlend();
	}
}
