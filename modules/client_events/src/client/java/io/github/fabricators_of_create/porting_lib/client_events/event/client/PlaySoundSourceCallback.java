//package io.github.fabricators_of_create.porting_lib.client_events.event.client;
//
//import com.mojang.blaze3d.audio.Channel;
//
//import net.fabricmc.fabric.api.event.Event;
//import net.fabricmc.fabric.api.event.EventFactory;
//import net.minecraft.client.resources.sounds.SoundInstance;
//import net.minecraft.client.sounds.SoundEngine;
//
///**
// * Fired when a <em>non-streaming</em> sound is being played. A non-streaming sound is loaded fully into memory
// * in a buffer before being played, and used for most sounds of short length such as sound effects for clicking
// * buttons.
// */
//public interface PlaySoundSourceCallback {
//	Event<PlaySoundSourceCallback> EVENT = EventFactory.createArrayBacked(PlaySoundSourceCallback.class, callbacks -> (engine, sound, channel) -> {
//		for (PlaySoundSourceCallback e : callbacks)
//			e.onPlaySoundSource(engine, sound, channel);
//	});
//
//	/**
//	 * @param engine The {@link SoundEngine} handling the sound.
//	 * @param sound The {@link SoundInstance} that will be played.
//	 * @param channel The {@link Channel} the sound will be played in.
//	 */
//	void onPlaySoundSource(SoundEngine engine, SoundInstance sound, Channel channel);
//}
