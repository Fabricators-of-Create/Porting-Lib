package io.github.fabricators_of_create.porting_lib.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.fabricators_of_create.porting_lib.event.client.KeyInputCallback;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyboardHandler;

@Environment(EnvType.CLIENT)
@Mixin(KeyboardHandler.class)
public abstract class KeyboardHandlerMixin {
	// First return opcode is jumped over if condition is met.
	@Inject(
			method = "keyPress",
			slice = @Slice(
					from = @At(
							value = "RETURN",
							ordinal = 0,
							shift = Shift.AFTER
					)
			),
			at = @At(value = "RETURN")
	)
	public void port_lib$onHandleKeyInput(long window, int key, int scancode, int action, int mods, CallbackInfo ci) {
		KeyInputCallback.EVENT.invoker().onKeyInput(key, scancode, action, mods);
	}
}
