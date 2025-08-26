package io.github.fabricators_of_create.porting_lib.gui.events;

import io.github.fabricators_of_create.porting_lib.core.event.CancellableEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Fired when the HUD is rendered to the screen.
 * See the two subclasses for listening to the two possible phases.
 *
 * @see Pre
 * @see Post
 */
public interface RenderGuiCallback {
	Event<Pre> PRE = EventFactory.createArrayBacked(Pre.class, callbacks -> (guiGraphics, partialTick) -> {
		boolean isCancelled = false;

		for (Pre callback : callbacks) {
			if (callback.preRenderGui(guiGraphics, partialTick)) {
				isCancelled = true;
			}
		}

		return isCancelled;
	});

	Event<Post> POST = EventFactory.createArrayBacked(Post.class, callbacks -> (guiGraphics, partialTick) -> {
		for (Post callback : callbacks) {
			callback.postRenderGui(guiGraphics, partialTick);
		}
	});

	/**
	 * Fired <b>before</b> the HUD is rendered to the screen.
	 *
	 * <p>This event is {@linkplain CancellableEvent cancellable}, and does not have a result.
	 * If this event is cancelled, then the callback will return true, the overlay will not be rendered, and the corresponding {@link Post} event will
	 * not be fired.</p>
	 *
	 * <p>This event is fired only on the {@linkplain EnvType#CLIENT logical client}.</p>
	 *
	 * @see Post
	 */
	interface Pre {
		boolean preRenderGui(GuiGraphics guiGraphics, DeltaTracker partialTick);
	}

	/**
	 * Fired <b>after</b> the HUD is rendered to the screen, if the corresponding {@link Pre} is not cancelled.
	 *
	 * <p>This event is not {@linkplain CancellableEvent cancellable}, and does not have a result.</p>
	 *
	 * <p>This event is fired only on the {@linkplain EnvType#CLIENT logical client}.</p>
	 */
	interface Post {
		void postRenderGui(GuiGraphics guiGraphics, DeltaTracker partialTick);
	}
}
