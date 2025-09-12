package io.github.fabricators_of_create.porting_lib.client_events.event.client;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;
import io.github.fabricators_of_create.porting_lib.core.event.CancellableEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.Music;

import org.jetbrains.annotations.Nullable;

/**
 * Fired when the {@link net.minecraft.client.sounds.MusicManager} checks what situational music should be used. This fires before the music begins playing.<br>
 * If the music is set to {@code null} by a modder, it will cancel any music that was already playing.<br>
 * <br>
 * Note that the higher priority you make your event listener, the earlier the music will be set.<br>
 * Because of this, if you want your music to take precedence over others (perhaps you want to have seperate nighttime music for a biome for instance) then you may want it to have a lower priority.<br>
 * <br>
 * To make your music instantly play rather than waiting for the playing music to stop, set the music to one that {@linkplain Music#replaceCurrentMusic() is set to replace the current music.}<br>
 * <br>
 * Higher priorities would likely be better suited for biome-based or dimension-based musics, whereas lower priority is likely good for specific structures or situations.<br>
 * <br>
 * This event is {@linkplain CancellableEvent cancellable}, and does not have a result.<br>
 * If the event is canceled, then whatever the latest music set was will be used as the music.
 * <br>
 * This event is only on the {@linkplain EnvType#CLIENT logical client}.<br>
 *
 */
public class SelectMusicEvent extends BaseEvent implements CancellableEvent {
	public static final Event<SelectMusicCallback> EVENT = EventFactory.createArrayBacked(SelectMusicCallback.class, callbacks -> event -> {
		for (final SelectMusicCallback callback : callbacks) {
			callback.onSelectMusic(event);
		}
	});

	private @Nullable Music music;
	private final Music originalMusic;
	private final @Nullable SoundInstance playingMusic;

	public SelectMusicEvent(Music music, @Nullable SoundInstance playingMusic) {
		this.music = music;
		this.originalMusic = music;
		this.playingMusic = playingMusic;
	}

	/**
	 * {@return the original situational music that was selected}
	 */
	public Music getOriginalMusic() {
		return originalMusic;
	}

	/**
	 * {@return the current track that the {@link net.minecraft.client.sounds.MusicManager} is playing, or {@code null} if there is none}
	 */
	@Nullable
	public SoundInstance getPlayingMusic() {
		return playingMusic;
	}

	/**
	 * {@return the Music to be played, or {@code null} if any playing music should be cancelled}
	 */
	@Nullable
	public Music getMusic() {
		return music;
	}

	/**
	 * Changes the situational music. If this is set to {@code null}, any currently playing music will be cancelled.<br>
	 * If this <i>was</i> {@code null} but on the next tick isn't, the music given will be immediately played.<br>
	 * <br>
	 */
	public void setMusic(@Nullable Music newMusic) {
		this.music = newMusic;
	}

	/**
	 * Sets the music and then cancels the event so that other listeners will not be invoked.<br>
	 * Note that listeners using {@link SubscribeEvent#receiveCanceled()} will still be able to override this, but by default they will not
	 */
	public void overrideMusic(@Nullable Music newMusic) {
		this.music = newMusic;
		this.setCanceled(true);
	}

	@Override
	public void sendEvent() {
		EVENT.invoker().onSelectMusic(this);
	}

	public interface SelectMusicCallback {
		void onSelectMusic(SelectMusicEvent event);
	}
}
