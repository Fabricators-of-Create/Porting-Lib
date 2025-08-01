package io.github.fabricators_of_create.porting_lib.client_events.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.client_events.event.client.InputEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;

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

	@Shadow
	public abstract boolean isLeftPressed();

	@Shadow
	public abstract boolean isMiddlePressed();

	@Shadow
	public abstract boolean isRightPressed();

	@Shadow
	public abstract double xpos();

	@Shadow
	public abstract double ypos();

	@Inject(method = "onPress", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getOverlay()Lnet/minecraft/client/gui/screens/Overlay;"), cancellable = true)
	private void port_lib$onMouseButtonPre(long windowPointer, int button, int action, int modifiers, CallbackInfo ci) {
		InputEvent.MouseButton.Pre event = new InputEvent.MouseButton.Pre(button, action, modifiers);
		event.sendEvent();

		if (event.isCanceled()) {
			ci.cancel();
		}
	}

	@Inject(method = "onPress", at = @At("TAIL"))
	private void port_lib$onMouseButtonPost(long windowPointer, int button, int action, int modifiers, CallbackInfo ci) {
		if (windowPointer == this.minecraft.getWindow().getWindow()) {
			InputEvent.MouseButton.Post event = new InputEvent.MouseButton.Post(button, action, modifiers);
			event.sendEvent();
		}
	}

	@Inject(method = "onScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z", ordinal = 0), cancellable = true)
	private void port_lib$onMouseScroll(long windowPointer, double xOffset, double yOffset, CallbackInfo ci, @Local(ordinal = 3) double scrollDeltaX, @Local(ordinal = 4) double scrollDeltaY) {
		InputEvent.MouseScrollingEvent event = new InputEvent.MouseScrollingEvent(scrollDeltaX, scrollDeltaY, this.isLeftPressed(), this.isMiddlePressed(), this.isRightPressed(), this.xpos(), this.ypos());
		event.sendEvent();

		if (event.isCanceled()) {
			ci.cancel();
		}
	}
}
