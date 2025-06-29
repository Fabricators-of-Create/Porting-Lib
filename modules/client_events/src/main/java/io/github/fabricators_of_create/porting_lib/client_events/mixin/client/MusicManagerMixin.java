package io.github.fabricators_of_create.porting_lib.client_events.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.client_events.event.client.SelectMusicEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.MusicInfo;
import net.minecraft.client.sounds.MusicManager;

import net.minecraft.sounds.Music;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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

	@WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getSituationalMusic()Lnet/minecraft/client/sounds/MusicInfo;"))
	private MusicInfo selectMusicEvent(Minecraft instance, Operation<MusicInfo> original) {
		SelectMusicEvent event = new SelectMusicEvent(original.call(instance), this.currentMusic);
		event.sendEvent();
		return event.getMusic();
	}

	@Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/sounds/MusicManager;currentMusic:Lnet/minecraft/client/resources/sounds/SoundInstance;", ordinal = 0), cancellable = true)
	private void checkIfMusicIsNull(CallbackInfo ci, @Local(ordinal = 0) MusicInfo music) {
		if (music == null) {
			if (this.currentMusic != null) {
				stopPlaying();
			}
			this.nextSongDelay = 0;
			ci.cancel();
		}
	}
}
