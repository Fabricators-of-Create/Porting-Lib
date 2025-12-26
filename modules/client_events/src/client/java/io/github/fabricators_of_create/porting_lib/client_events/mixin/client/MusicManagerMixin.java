package io.github.fabricators_of_create.porting_lib.client_events.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import com.llamalad7.mixinextras.sugar.Cancellable;

import io.github.fabricators_of_create.porting_lib.client_events.ClientEventHooks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.MusicManager;

import net.minecraft.sounds.Music;

import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MusicManager.class)
public abstract class MusicManagerMixin {
	@Shadow
	@Nullable
	private SoundInstance currentMusic;

	@Shadow
	public abstract void stopPlaying();

	@Shadow
	private int nextSongDelay;

	@WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getSituationalMusic()Lnet/minecraft/sounds/Music;"))
	private Music selectMusicEvent(Minecraft instance, Operation<Music> original, @Cancellable CallbackInfo ci) {
		Music music = ClientEventHooks.selectMusic(original.call(instance), this.currentMusic);
		if (music == null) {
			if (this.currentMusic != null) {
				this.stopPlaying();
			}
			this.nextSongDelay = 0;
			ci.cancel();
		}
		return music;
	}
}
