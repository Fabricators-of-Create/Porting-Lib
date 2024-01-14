package io.github.fabricators_of_create.porting_lib.client_events.event.client;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;

/**
 * Fired when a sound is about to be played by the sound engine. This fires before the sound is played and before any
 * checks on the sound (such as a zeroed volume, an empty {@link net.minecraft.client.resources.sounds.Sound}, and
 * others). This can be used to change or prevent (by passing {@code null)} a sound from being played by returning null.
 *
 * @see PlaySoundSourceCallback
 * @see PlayStreamingSourceEvent
 */
public interface PlaySoundCallback {
	Event<PlaySoundCallback> EVENT = EventFactory.createArrayBacked(PlaySoundCallback.class, callbacks -> (manager, sound, originalSound) -> {
		SoundInstance newSound = originalSound;
		for (PlaySoundCallback e : callbacks)
			newSound = e.onPlaySound(manager, sound, originalSound);
		return newSound;
	});

	/**
	 * @param engine The {@link SoundEngine} handling the sound.
	 * @param sound The {@link SoundInstance} that will be played.
	 * @param originalSound The original {@link SoundInstance}; will be equal to {@code sound} if the sound hasn't been modified by another mod.
	 * @return The sound that should be played, or null if nothing should be played.
	 */
	SoundInstance onPlaySound(SoundEngine engine, SoundInstance sound, SoundInstance originalSound);
}
