package io.github.fabricators_of_create.porting_lib.client.mixin;

import io.github.fabricators_of_create.porting_lib.client.event.OverlayRenderCallback;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

@Environment(EnvType.CLIENT)
@Mixin(Gui.class)
public abstract class GuiMixin {
	@Shadow
	@Final
	private Minecraft minecraft;

	@Unique
	private float partialTicks;

	@Inject(method = "render", at = @At("HEAD"))
	public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		partialTicks = deltaTracker.getGameTimeDeltaTicks();
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
	private void renderStatusBars(GuiGraphics guiGraphics, CallbackInfo ci) {
		if (OverlayRenderCallback.EVENT.invoker().onOverlayRender(guiGraphics, partialTicks, minecraft.getWindow(), OverlayRenderCallback.Types.AIR)) {
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
	private void renderHealth(GuiGraphics guiGraphics, Player player, int i, int j, int k, int l, float f, int m, int n, int o, boolean bl, CallbackInfo ci) {
		if (OverlayRenderCallback.EVENT.invoker().onOverlayRender(guiGraphics, partialTicks, minecraft.getWindow(), OverlayRenderCallback.Types.PLAYER_HEALTH)) {
			ci.cancel();
		}
	}

	@Inject(method = "renderCrosshair", at = @At("HEAD"), cancellable = true)
	private void renderCrosshair(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
		if (OverlayRenderCallback.EVENT.invoker().onOverlayRender(guiGraphics, partialTicks, minecraft.getWindow(), OverlayRenderCallback.Types.CROSSHAIRS)) {
			ci.cancel();
		}
	}
}
