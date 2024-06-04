package io.github.fabricators_of_create.porting_lib.client_events.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import io.github.fabricators_of_create.porting_lib.client_events.event.client.PlaySoundCallback;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;

@Mixin(SoundEngine.class)
public class SoundEngineMixin {
	@Shadow
	private boolean loaded;

	@ModifyVariable(method = "play", at = @At("HEAD"), argsOnly = true, index = 1)
	private SoundInstance modifySound(SoundInstance value) {
		if (this.loaded)
			return PlaySoundCallback.EVENT.invoker().onPlaySound((SoundEngine) (Object) this, value, value);
		return value;
	}

	@WrapOperation(method = "play", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/sounds/SoundInstance;canPlaySound()Z"))
	private boolean allowNullSound(SoundInstance instance, Operation<Boolean> original) {
		if (instance != null)
			return original.call(instance);
		return false;
	}

//	@Inject(method = "method_19752", at = @At("TAIL"))
//	private static void playSoundSource(SoundBuffer soundBuffer, Channel channel, CallbackInfo ci) {
//		PlaySoundSourceCallback.EVENT.invoker().onPlaySoundSource(null, null, channel);
//	}
}
