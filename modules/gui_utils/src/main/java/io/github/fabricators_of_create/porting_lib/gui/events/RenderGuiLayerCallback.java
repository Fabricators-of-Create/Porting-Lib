package io.github.fabricators_of_create.porting_lib.gui.events;

import io.github.fabricators_of_create.porting_lib.core.event.CancellableEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;

/**
 * Fired when a GUI layer is rendered to the screen.
 * See the two subclasses for listening to the two possible phases.
 *
 * <p>A layer that is not normally active (for example because the player pressed F1) cannot be forced to render.
 * In such cases, this event will however still fire.
 *
 * @see Pre
 * @see Post
 */
public interface RenderGuiLayerCallback {
	Event<Pre> PRE = EventFactory.createArrayBacked(Pre.class, callbacks -> (guiGraphics, partialTick, name, layer) -> {
		boolean isCancelled = false;

		for (Pre callback : callbacks) {
			if (callback.preRenderGuiLayer(guiGraphics, partialTick, name, layer)) {
				isCancelled = true;
			}
		}

		return isCancelled;
	});

	Event<Post> POST = EventFactory.createArrayBacked(Post.class, callbacks -> (guiGraphics, partialTick, name, layer) -> {
		for (Post callback : callbacks) {
			callback.postRenderGuiLayer(guiGraphics, partialTick, name, layer);
		}
	});

	/**
	 * Fired <b>before</b> a GUI layer is rendered to the screen.
	 *
	 * <p>This event is {@linkplain CancellableEvent cancellable}, and does not have a result.
	 * If this event is cancelled, then the callback will return true, the layer will not be rendered, and the corresponding {@link Post} event will
	 * not be fired.</p>
	 *
	 * <p>This event is fired only on the {@linkplain EnvType#CLIENT logical client}.</p>
	 *
	 * @see Post
	 */
	interface Pre {
		boolean preRenderGuiLayer(GuiGraphics guiGraphics, DeltaTracker partialTick, ResourceLocation name, LayeredDraw.Layer layer);
	}

	/**
	 * Fired <b>after</b> a GUI layer is rendered to the screen, if the corresponding {@link Pre} is not cancelled.
	 *
	 * <p>This event is not {@linkplain CancellableEvent cancellable}, and does not have a result.</p>
	 *
	 * <p>This event is fired only on the {@linkplain EnvType#CLIENT logical client}.</p>
	 */
	interface Post {
		void postRenderGuiLayer(GuiGraphics guiGraphics, DeltaTracker partialTick, ResourceLocation name, LayeredDraw.Layer layer);
	}
}
