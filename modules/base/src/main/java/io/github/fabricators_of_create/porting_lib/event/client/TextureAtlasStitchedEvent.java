package io.github.fabricators_of_create.porting_lib.event.client;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;
import io.github.fabricators_of_create.porting_lib.core.event.CancellableEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.texture.TextureAtlas;

import org.jetbrains.annotations.ApiStatus;

/**
 * Fired <b>after</b> a texture atlas is stitched together and all textures therein have been loaded.
 *
 * <p>This event is not {@linkplain CancellableEvent cancellable}.</p>
 *
 * <p>This event is fired on the mod-specific event bus, only on the {@linkplain EnvType#CLIENT logical client}.</p>
 *
 * @see TextureAtlas
 */
public class TextureAtlasStitchedEvent extends BaseEvent {
	public static final Event<Callback> EVENT = EventFactory.createArrayBacked(Callback.class, callbacks -> event -> {
		for (Callback callback : callbacks)
			callback.onStitched(event);
	});

	private final TextureAtlas atlas;

	@ApiStatus.Internal
	public TextureAtlasStitchedEvent(TextureAtlas atlas) {
		this.atlas = atlas;
	}

	/**
	 * {@return the texture atlas}
	 */
	public TextureAtlas getAtlas() {
		return atlas;
	}

	@Override
	public void sendEvent() {
		EVENT.invoker().onStitched(this);
	}

	public interface Callback {
		void onStitched(TextureAtlasStitchedEvent event);
	}
}
