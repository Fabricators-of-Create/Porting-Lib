package io.github.fabricators_of_create.porting_lib.gui.layered;

import com.google.common.base.Preconditions;
import io.github.fabricators_of_create.porting_lib.gui.events.RenderGuiCallback;
import io.github.fabricators_of_create.porting_lib.gui.events.RenderGuiLayerCallback;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;

import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

/**
 * Adaptation of {@link LayeredDraw} that is used for {@link Gui} rendering specifically,
 * to give layers a name and fire appropriate events.
 *
 * <p>Overlays can be registered using the {@link GuiLayerRegistry}.
 */
@ApiStatus.Internal
public class GuiLayerManager {
	public static final float Z_SEPARATION = LayeredDraw.Z_SEPARATION;
	private final List<NamedLayer> layers = new ArrayList<>();

	public record NamedLayer(ResourceLocation name, LayeredDraw.Layer layer, boolean isVanilla) {
		public NamedLayer(ResourceLocation name, LayeredDraw.Layer layer) {
			this(name, layer, false);
		}
	}

	public GuiLayerManager add(ResourceLocation name, LayeredDraw.Layer layer) {
		this.layers.add(new NamedLayer(name, layer));
		return this;
	}

	public GuiLayerManager add(ResourceLocation name, LayeredDraw.Layer layer, boolean isVanilla) {
		this.layers.add(new NamedLayer(name, layer, isVanilla));
		return this;
	}

	public GuiLayerManager add(GuiLayerManager child, BooleanSupplier shouldRender) {
		// Flatten the layers to allow mods to insert layers between vanilla layers.
		for (var entry : child.layers) {
			add(entry.name(), (guiGraphics, partialTick) -> {
				if (shouldRender.getAsBoolean()) {
					entry.layer().render(guiGraphics, partialTick);
				}
			}, entry.isVanilla());
		}
		return this;
	}

	public GuiLayerManager addVanilla(ResourceLocation name) {
		add(name, (guiGraphics, deltaTracker) -> {}, true);
		return this;
	}

	public GuiLayerManager addVanilla(ResourceLocation name, LayeredDraw.Layer layer) {
		add(name, layer, true);
		return this;
	}

	// Doesn't actually get called, but if another mod for whatever reason uses this, welp.
	public void render(GuiGraphics guiGraphics, DeltaTracker partialTick) {
		if (RenderGuiCallback.PRE.invoker().preRenderGui(guiGraphics, partialTick)) {
			return;
		}

		renderInner(guiGraphics, partialTick);

		RenderGuiCallback.POST.invoker().postRenderGui(guiGraphics, partialTick);
	}

	private void renderInner(GuiGraphics guiGraphics, DeltaTracker partialTick) {
		guiGraphics.pose().pushPose();

		for (var layer : this.layers) {
			renderLayer(guiGraphics, partialTick, layer);
		}

		guiGraphics.pose().popPose();
	}

	/**
	 * Renders layers starting from one render layer until the next Vanilla layer is reached.
	 * @param start The ID of the rendering layer to start from
	 */
	public void renderFrom(ResourceLocation start, GuiGraphics guiGraphics, DeltaTracker partialTick) {
		NamedLayer startingLayer = getLayer(start);
		if (startingLayer == null) {
			throw new IllegalArgumentException("Layer " + start + " does not exist!");
		}

		renderFrom(startingLayer, guiGraphics, partialTick);
	}

	public void renderFrom(NamedLayer startingLayer, GuiGraphics guiGraphics, DeltaTracker partialTick) {
		guiGraphics.pose().pushPose();
		boolean hasStartedRendering = false;

		for (NamedLayer layer : layers) {
			if (layer == startingLayer || startingLayer == null) {
				hasStartedRendering = true;
			}

			if (!hasStartedRendering) {
				continue;
			}

			// Stop rendering entirely if this layer is a Vanilla layer that isn't the starting layer.
			if (layer.isVanilla() && layer != startingLayer) {
				break;
			}

			// Render only non-Vanilla layers - we may end up double-rendering otherwise.
			if (!layer.isVanilla()) {
				renderLayer(guiGraphics, partialTick, layer);
			}
		}
		guiGraphics.pose().popPose();
	}

	public boolean callPreRenderEvent(ResourceLocation id, GuiGraphics guiGraphics, DeltaTracker partialTick) {
		NamedLayer layer = getLayer(id);

		if (layer == null) {
			throw new IllegalArgumentException("Layer " + id + " does not exist!");
		}

		return RenderGuiLayerCallback.PRE.invoker().preRenderGuiLayer(guiGraphics, partialTick, layer.name(), layer.layer());
	}

	public void callPostRenderEvent(ResourceLocation id, GuiGraphics guiGraphics, DeltaTracker partialTick) {
		NamedLayer layer = getLayer(id);

		if (layer == null) {
			throw new IllegalArgumentException("Layer " + id + " does not exist!");
		}

		RenderGuiLayerCallback.POST.invoker().postRenderGuiLayer(guiGraphics, partialTick, layer.name(), layer.layer());
	}

	private void renderLayer(GuiGraphics guiGraphics, DeltaTracker partialTick, NamedLayer layer) {
		if (!RenderGuiLayerCallback.PRE.invoker().preRenderGuiLayer(guiGraphics, partialTick, layer.name(), layer.layer())) {
			layer.layer().render(guiGraphics, partialTick);
			RenderGuiLayerCallback.POST.invoker().postRenderGuiLayer(guiGraphics, partialTick, layer.name(), layer.layer());
		}

		guiGraphics.pose().translate(0.0F, 0.0F, Z_SEPARATION);
	}

	public NamedLayer getLayer(ResourceLocation id) {
		for (NamedLayer layer : layers) {
			if (layer.name().equals(id)) {
				return layer;
			}
		}

		return null;
	}

	public int getLayerCount() {
		return layers.size();
	}

	/**
	 * Wrap the layer with the given {@code id} in a new layer.
	 * <p>
	 * This can be used, for instance, to apply pose stack transformations to move the layer or resize it.
	 *
	 * @param id      the id of the layer to wrap
	 * @param wrapper an unary operator which takes in the old layer and returns the new layer that wraps the old one
	 * @throws IllegalArgumentException if a layer with the given {@code id} is not yet registered
	 */
	public void wrapLayer(ResourceLocation id, UnaryOperator<LayeredDraw.Layer> wrapper) {
		Objects.requireNonNull(id);
		Objects.requireNonNull(wrapper);

		for (int i = 0; i < layers.size(); i++) {
			var layer = layers.get(i);
			if (layer.name().equals(id)) {
				var wrapped = wrapper.apply(layer.layer());
				Objects.requireNonNull(wrapped, "wrapping layer must not be null");
				layers.set(i, new GuiLayerManager.NamedLayer(id, wrapped));
				return;
			}
		}

		throw new IllegalArgumentException("Attempted to wrap layer with id '" + id + "', which does not exist!");
	}

	public void register(GuiLayerRegistry.Ordering ordering, @Nullable ResourceLocation other, ResourceLocation key, LayeredDraw.Layer layer) {
		Objects.requireNonNull(key);
		for (var namedLayer : layers) {
			Preconditions.checkArgument(!namedLayer.name().equals(key), "Layer already registered: " + key);
		}

		int insertPosition;
		if (other == null) {
			insertPosition = ordering == GuiLayerRegistry.Ordering.BEFORE ? 0 : layers.size();
		} else {
			var otherIndex = IntStream.range(0, layers.size())
					.filter(i -> layers.get(i).name().equals(other))
					.findFirst();
			if (otherIndex.isEmpty()) {
				throw new IllegalArgumentException("Attempted to order against an unregistered layer " + other + ". Only order against vanilla's and your own.");
			}

			insertPosition = otherIndex.getAsInt() + (ordering == GuiLayerRegistry.Ordering.BEFORE ? 0 : 1);
		}

		layers.add(insertPosition, new GuiLayerManager.NamedLayer(key, layer));
	}
}
