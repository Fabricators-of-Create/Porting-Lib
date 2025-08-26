package io.github.fabricators_of_create.porting_lib.gui.layered;

import io.github.fabricators_of_create.porting_lib.gui.events.RenderGuiLayerEvent;
import io.github.fabricators_of_create.porting_lib.gui.mixin.GuiAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;

import java.util.function.UnaryOperator;

import static io.github.fabricators_of_create.porting_lib.gui.layered.VanillaGuiLayers.*;

/**
 * Allows users to register custom {@link LayeredDraw.Layer layers} for GUI rendering.
 *
 * <p>See also {@link RenderGuiLayerEvent} to intercept rendering of registered layers.
 */
public class GuiLayerRegistry {
	private static final GuiLayerManager layerManager = new GuiLayerManager();

	public static GuiLayerManager getLayerManager() {
		return layerManager;
	}

	static {
		GuiLayerManager playerHealthComponents = new GuiLayerManager()
				.addVanilla(PLAYER_HEALTH)
				.addVanilla(ARMOR_LEVEL)
				.addVanilla(FOOD_LEVEL);

		GuiLayerManager mainLayers = new GuiLayerManager()
				.addVanilla(CAMERA_OVERLAYS, ((GuiAccessor) getGui())::callRenderCameraOverlays)
				.addVanilla(CROSSHAIR, ((GuiAccessor) getGui())::callRenderCrosshair)
				.addVanilla(HOTBAR)
				.addVanilla(JUMP_METER)
				.addVanilla(EXPERIENCE_BAR)
				.add(playerHealthComponents, () -> Minecraft.getInstance().gameMode.canHurtPlayer())
				.addVanilla(VEHICLE_HEALTH)
				.addVanilla(AIR_LEVEL)
				.addVanilla(SELECTED_ITEM_NAME)
				.addVanilla(SPECTATOR_TOOLTIP)
				.addVanilla(EXPERIENCE_LEVEL, ((GuiAccessor) getGui())::callRenderExperienceLevel)
				.addVanilla(EFFECTS, ((GuiAccessor) getGui())::callRenderEffects)
				.addVanilla(BOSS_OVERLAY, (guiGraphics, tickDelta) -> getGui().getBossOverlay().render(guiGraphics));

		GuiLayerManager additionalLayers = new GuiLayerManager()
				.addVanilla(DEMO_OVERLAY, ((GuiAccessor) getGui())::callRenderDemoOverlay)
				.addVanilla(DEBUG_OVERLAY, (guiGraphics, deltaTracker) -> {
					Gui gui = getGui();
					if (gui.getDebugOverlay().showDebugScreen()) {
						gui.getDebugOverlay().render(guiGraphics);
					}
				})
				.addVanilla(SCOREBOARD_SIDEBAR, ((GuiAccessor) getGui())::callRenderScoreboardSidebar)
				.addVanilla(OVERLAY_MESSAGE, ((GuiAccessor) getGui())::callRenderOverlayMessage)
				.addVanilla(TITLE, ((GuiAccessor) getGui())::callRenderTitle)
				.addVanilla(CHAT, ((GuiAccessor) getGui())::callRenderChat)
				.addVanilla(TAB_LIST, ((GuiAccessor) getGui())::callRenderTabList)
				.addVanilla(SUBTITLE_OVERLAY, (guiGraphics, deltaTracker) -> ((GuiAccessor) getGui()).getSubtitleOverlay().render(guiGraphics))
				.addVanilla(SAVING_INDICATOR, getGui()::renderSavingIndicator);

		layerManager
				.add(mainLayers, () -> !Minecraft.getInstance().options.hideGui)
				.addVanilla(SLEEP_OVERLAY, ((GuiAccessor) getGui())::callRenderSleepOverlay)
				.add(additionalLayers, () -> !Minecraft.getInstance().options.hideGui);
	}

	/**
	 * Registers a layer that renders below all others.
	 *
	 * @param id    A unique resource id for this layer
	 * @param layer The layer
	 */
	public static void registerBelowAll(ResourceLocation id, LayeredDraw.Layer layer) {
		getLayerManager().register(Ordering.BEFORE, null, id, layer);
	}

	/**
	 * Registers a layer that renders below another.
	 *
	 * @param other The id of the layer to render below. This must be a layer you have already registered or one of the
	 *              {@link VanillaGuiLayers vanilla layers}. Do not use other mods' layers.
	 * @param id    A unique resource id for this layer
	 * @param layer The layer
	 */
	public static void registerBelow(ResourceLocation other, ResourceLocation id, LayeredDraw.Layer layer) {
		getLayerManager().register(Ordering.BEFORE, other, id, layer);
	}

	/**
	 * Registers an layer that renders above another.
	 *
	 * @param other The id of the layer to render above. This must be a layer you have already registered or one of the
	 *              {@link VanillaGuiLayers vanilla layers}. Do not use other mods' layers.
	 * @param id    A unique resource id for this layer
	 * @param layer The layer
	 */
	public static void registerAbove(ResourceLocation other, ResourceLocation id, LayeredDraw.Layer layer) {
		getLayerManager().register(Ordering.AFTER, other, id, layer);
	}

	/**
	 * Registers a layer that renders above all others.
	 *
	 * @param id    A unique resource id for this layer
	 * @param layer The layer
	 */
	public static void registerAboveAll(ResourceLocation id, LayeredDraw.Layer layer) {
		getLayerManager().register(Ordering.AFTER, null, id, layer);
	}

	/**
	 * Replace the layer with the given {@code id} with a new one.
	 *
	 * @param id          the id of the layer to replace
	 * @param replacement the layer to replace it with
	 * @throws IllegalArgumentException if a layer with the given {@code id} is not yet registered
	 * @see #wrapLayer(ResourceLocation, UnaryOperator) use {@code wrapLayer} if you'd like to
	 *      wrap the layer to apply pose stack transformations
	 */
	public static void replaceLayer(ResourceLocation id, LayeredDraw.Layer replacement) {
		wrapLayer(id, old -> replacement);
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
	public static void wrapLayer(ResourceLocation id, UnaryOperator<LayeredDraw.Layer> wrapper) {
		getLayerManager().wrapLayer(id, wrapper);
	}

	private static Gui getGui() {
		return Minecraft.getInstance().gui;
	}

	public enum Ordering {
		BEFORE, AFTER
	}
}
