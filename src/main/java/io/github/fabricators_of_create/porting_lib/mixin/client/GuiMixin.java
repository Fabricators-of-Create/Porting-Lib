package io.github.fabricators_of_create.porting_lib.mixin.client;

import static net.minecraft.client.gui.GuiComponent.GUI_ICONS_LOCATION;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.fabricators_of_create.porting_lib.event.OverlayRenderCallback;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GameRenderer;

@Environment(EnvType.CLIENT)
@Mixin(Gui.class)
public abstract class GuiMixin {
	@Shadow
	@Final
	private Minecraft minecraft;
	@Unique
	public float port_lib$partialTicks;

	@Inject(method = "render", at = @At("HEAD"))
	public void port_lib$render(PoseStack matrixStack, float f, CallbackInfo ci) {
		port_lib$partialTicks = f;
	}

	//This might be the wrong method to inject to
	@Inject(
			method = "renderPlayerHealth",
			at = @At(
					value = "INVOKE",
					shift = At.Shift.AFTER,
					target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V"
			)
	)
	private void port_lib$renderStatusBars(PoseStack matrixStack, CallbackInfo ci) {
		OverlayRenderCallback.EVENT.invoker().onOverlayRender(matrixStack, port_lib$partialTicks, minecraft.getWindow(), OverlayRenderCallback.Types.AIR);
	}

	@Inject(method = "renderCrosshair", at = @At("HEAD"))
	private void port_lib$renderCrosshair(PoseStack matrixStack, CallbackInfo ci) {
		OverlayRenderCallback.EVENT.invoker().onOverlayRender(matrixStack, port_lib$partialTicks, minecraft.getWindow(), OverlayRenderCallback.Types.CROSSHAIRS);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, GUI_ICONS_LOCATION);
		RenderSystem.enableBlend();
	}
}
