package io.github.fabricators_of_create.porting_lib.gui.events;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;
import io.github.fabricators_of_create.porting_lib.core.event.CancellableEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

import org.jetbrains.annotations.ApiStatus;

/**
 * Fired when the HUD is rendered to the screen.
 * See the two subclasses for listening to the two possible phases.
 *
 * @see Pre
 * @see Post
 */
public abstract class RenderGuiEvent extends BaseEvent {
	public static final Event<Pre.Callback> PRE = EventFactory.createArrayBacked(Pre.Callback.class, callbacks -> event -> {
		for (Pre.Callback callback : callbacks) {
			callback.preRenderGui(event);
		}
	});

	public static final Event<Post.Callback> POST = EventFactory.createArrayBacked(Post.Callback.class, callbacks -> event -> {
		for (Post.Callback callback : callbacks) {
			callback.postRenderGui(event);
		}
	});

	private final GuiGraphics guiGraphics;
	private final DeltaTracker partialTick;

	@ApiStatus.Internal
	protected RenderGuiEvent(GuiGraphics guiGraphics, DeltaTracker partialTick) {
		this.guiGraphics = guiGraphics;
		this.partialTick = partialTick;
	}

	public GuiGraphics getGuiGraphics() {
		return guiGraphics;
	}

	public DeltaTracker getPartialTick() {
		return partialTick;
	}

	/**
	 * Fired <b>before</b> the HUD is rendered to the screen.
	 *
	 * <p>This event is {@linkplain CancellableEvent cancellable}, and does not have a result.
	 * If this event is cancelled, then the overlay will not be rendered, and the corresponding {@link Post} event will
	 * not be fired.</p>
	 *
	 * <p>This event is fired only on the {@linkplain EnvType#CLIENT logical client}.</p>
	 *
	 * @see Post
	 */
	public static class Pre extends RenderGuiEvent implements CancellableEvent {
		@ApiStatus.Internal
		public Pre(GuiGraphics guiGraphics, DeltaTracker partialTick) {
			super(guiGraphics, partialTick);
		}

		public interface Callback {
			void preRenderGui(Pre event);
		}

		@Override
		public void sendEvent() {
			PRE.invoker().preRenderGui(this);
		}
	}

	/**
	 * Fired <b>after</b> the HUD is rendered to the screen, if the corresponding {@link Pre} is not cancelled.
	 *
	 * <p>This event is not {@linkplain CancellableEvent cancellable}, and does not have a result.</p>
	 *
	 * <p>This event is fired only on the {@linkplain EnvType#CLIENT logical client}.</p>
	 */
	public static class Post extends RenderGuiEvent {
		@ApiStatus.Internal
		public Post(GuiGraphics guiGraphics, DeltaTracker partialTick) {
			super(guiGraphics, partialTick);
		}

		public interface Callback {
			void postRenderGui(Post event);
		}

		@Override
		public void sendEvent() {
			POST.invoker().postRenderGui(this);
		}
	}
}
