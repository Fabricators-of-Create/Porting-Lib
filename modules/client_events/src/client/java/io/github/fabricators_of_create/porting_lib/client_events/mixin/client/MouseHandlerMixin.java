package io.github.fabricators_of_create.porting_lib.client_events.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.client_events.ClientEventHooks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;

import net.minecraft.client.input.MouseButtonInfo;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {
	@Shadow
	@Final
	private Minecraft minecraft;

	@Inject(method = "onButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getOverlay()Lnet/minecraft/client/gui/screens/Overlay;"), cancellable = true)
	private void onMouseButtonPre(long window, MouseButtonInfo buttonInfo, int action, CallbackInfo ci) {
		if (ClientEventHooks.onMouseButtonPre(buttonInfo, action))
			ci.cancel();
	}

	@Inject(method = "onButton", at = @At("TAIL"))
	private void onMouseButtonPost(long window, MouseButtonInfo buttonInfo, int action, CallbackInfo ci) {
		if (window == this.minecraft.getWindow().handle()) {
			ClientEventHooks.onMouseButtonPost(buttonInfo, action);
		}
	}

	@Inject(method = "onScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z", ordinal = 0), cancellable = true)
	private void onMouseScroll(long windowPointer, double xOffset, double yOffset, CallbackInfo ci, @Local(ordinal = 3) double scrollDeltaX, @Local(ordinal = 4) double scrollDeltaY) {
		if (ClientEventHooks.onMouseScroll((MouseHandler) (Object) this, scrollDeltaX, scrollDeltaY))
			ci.cancel();
	}
}
