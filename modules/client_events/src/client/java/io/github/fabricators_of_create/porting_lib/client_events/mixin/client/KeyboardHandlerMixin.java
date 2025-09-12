package io.github.fabricators_of_create.porting_lib.client_events.mixin.client;

import io.github.fabricators_of_create.porting_lib.client_events.event.client.InputEvent;
import net.minecraft.client.KeyboardHandler;

import net.minecraft.client.Minecraft;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
	@Shadow
	@Final
	private Minecraft minecraft;

	@Inject(method = "keyPress", at = @At("TAIL"))
	private void port_lib$onKeyPressEvent(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
		if (windowPointer == minecraft.getWindow().getWindow()) {
			new InputEvent.Key(key, scanCode, action, modifiers).sendEvent();
		}
	}
}
