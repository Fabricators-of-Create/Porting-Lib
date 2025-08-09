package io.github.fabricators_of_create.porting_lib.gui.events;

import io.github.fabricators_of_create.porting_lib.core.event.BaseEvent;
import io.github.fabricators_of_create.porting_lib.core.event.CancellableEvent;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.ApiStatus;

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
public abstract class RenderGuiLayerEvent extends BaseEvent {
	public static final Event<Pre.Callback> PRE = EventFactory.createArrayBacked(Pre.Callback.class, callbacks -> event -> {
		for (Pre.Callback callback : callbacks) {
			callback.preRenderGuiLayer(event);
		}
	});

	public static final Event<Post.Callback> POST = EventFactory.createArrayBacked(Post.Callback.class, callbacks -> event -> {
		for (Post.Callback callback : callbacks) {
			callback.postRenderGuiLayer(event);
		}
	});

	private final GuiGraphics guiGraphics;
	private final DeltaTracker partialTick;
	private final ResourceLocation name;
	private final LayeredDraw.Layer layer;

	@ApiStatus.Internal
	protected RenderGuiLayerEvent(GuiGraphics guiGraphics, DeltaTracker partialTick, ResourceLocation name, LayeredDraw.Layer layer) {
		this.guiGraphics = guiGraphics;
		this.partialTick = partialTick;
		this.name = name;
		this.layer = layer;
	}

	public GuiGraphics getGuiGraphics() {
		return guiGraphics;
	}

	public DeltaTracker getPartialTick() {
		return partialTick;
	}

	public ResourceLocation getName() {
		return name;
	}

	public LayeredDraw.Layer getLayer() {
		return layer;
	}

	/**
	 * Fired <b>before</b> a GUI layer is rendered to the screen.
	 *
	 * <p>This event is {@linkplain CancellableEvent cancellable}, and does not have a result.
	 * If this event is cancelled, then the layer will not be rendered, and the corresponding {@link Post} event will
	 * not be fired.</p>
	 *
	 * <p>This event is fired only on the {@linkplain EnvType#CLIENT logical client}.</p>
	 *
	 * @see Post
	 */
	public static class Pre extends RenderGuiLayerEvent implements CancellableEvent {
		@ApiStatus.Internal
		public Pre(GuiGraphics guiGraphics, DeltaTracker partialTick, ResourceLocation name, LayeredDraw.Layer layer) {
			super(guiGraphics, partialTick, name, layer);
		}

		public interface Callback {
			void preRenderGuiLayer(RenderGuiLayerEvent.Pre event);
		}

		@Override
		public void sendEvent() {
			PRE.invoker().preRenderGuiLayer(this);
		}
	}

	/**
	 * Fired <b>after</b> a GUI layer is rendered to the screen, if the corresponding {@link Pre} is not cancelled.
	 *
	 * <p>This event is not {@linkplain CancellableEvent cancellable}, and does not have a result.</p>
	 *
	 * <p>This event is fired only on the {@linkplain EnvType#CLIENT logical client}.</p>
	 */
	public static class Post extends RenderGuiLayerEvent {
		@ApiStatus.Internal
		public Post(GuiGraphics guiGraphics, DeltaTracker partialTick, ResourceLocation name, LayeredDraw.Layer layer) {
			super(guiGraphics, partialTick, name, layer);
		}

		public interface Callback {
			void postRenderGuiLayer(RenderGuiLayerEvent.Post event);
		}

		@Override
		public void sendEvent() {
			POST.invoker().postRenderGuiLayer(this);
		}
	}
}
