package io.github.fabricators_of_create.porting_lib.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import io.github.fabricators_of_create.porting_lib.event.client.MouseButtonCallback;
import io.github.fabricators_of_create.porting_lib.event.client.MouseInputEvents;
import io.github.fabricators_of_create.porting_lib.event.client.MouseInputEvents.Action;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;

@Environment(EnvType.CLIENT)
@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {
	@Shadow
	@Final
	private Minecraft minecraft;

	@Inject(
			method = "onPress",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/Minecraft;getOverlay()Lnet/minecraft/client/gui/screens/Overlay;",
					ordinal = 0
			),
			cancellable = true
	)
	private void port_lib$beforeMouseButton(long windowPointer, int button, int action, int modifiers, CallbackInfo ci) {
		if (MouseInputEvents.BEFORE_BUTTON.invoker().beforeButtonPress(button, modifiers, Action.get(action))) {
			ci.cancel();
		}
	}

	@Inject(method = "onPress", at = @At("TAIL"))
	private void port_lib$afterMouseButton(long windowPointer, int button, int action, int modifiers, CallbackInfo ci) {
		// all processing is wrapped in this check, but since this inject targets tail, it's outside that. check again.
		if (windowPointer == minecraft.getWindow().getWindow()) {
			MouseInputEvents.AFTER_BUTTON.invoker().afterButtonPress(button, modifiers, Action.get(action));
		}
	}

	@Inject(
			method = "onScroll",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z"
			),
			locals = LocalCapture.CAPTURE_FAILHARD,
			cancellable = true
	)
	private void port_lib$beforeMouseScroll(long windowPointer, double xOffset, double yOffset, CallbackInfo ci, double deltaY) {
		double deltaX = port_lib$getDeltaX(xOffset);

		if (MouseInputEvents.BEFORE_SCROLL.invoker().beforeScroll(deltaX, deltaY)) {
			ci.cancel();
		}
	}

	@Inject(
			method = "onScroll",
			at = {
					@At(
							value = "INVOKE",
							target = "Lnet/minecraft/client/gui/screens/Screen;afterMouseAction()V",
							shift = Shift.AFTER
					),
					@At(
							value = "INVOKE",
							target = "Lnet/minecraft/client/gui/components/spectator/SpectatorGui;onMouseScrolled(I)V",
							shift = Shift.AFTER
					),
					@At(
							value = "INVOKE",
							target = "Lnet/minecraft/world/entity/player/Abilities;setFlyingSpeed(F)V",
							shift = Shift.AFTER
					),
					@At(
							value = "INVOKE",
							target = "Lnet/minecraft/world/entity/player/Inventory;swapPaint(D)V",
							shift = Shift.AFTER
					)
			},
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void port_lib$afterMouseScroll(long windowPointer, double xOffset, double yOffset, CallbackInfo ci, double deltaY) {
		double deltaX = port_lib$getDeltaX(xOffset);
		MouseInputEvents.AFTER_SCROLL.invoker().afterScroll(deltaX, deltaY);
	}

	@Unique
	private double port_lib$getDeltaX(double xOffset) {
		// copied processing for deltaY, but for X
		return (this.minecraft.options.discreteMouseScroll().get() ? Math.signum(xOffset) : xOffset)
				* this.minecraft.options.mouseWheelSensitivity().get();
	}

	// First return opcode is jumped over if condition is met.
	@Inject(
			method = "onPress",
			slice = @Slice(
					from = @At(
							value = "RETURN",
							ordinal = 0,
							shift = Shift.AFTER
					)
			),
			at = @At(value = "RETURN")
	)
	private void port_lib$onHandleMouseButton(long window, int button, int action, int modifiers, CallbackInfo ci) {
		MouseButtonCallback.EVENT.invoker().onMouseButton(button, action, modifiers);
	}
}
