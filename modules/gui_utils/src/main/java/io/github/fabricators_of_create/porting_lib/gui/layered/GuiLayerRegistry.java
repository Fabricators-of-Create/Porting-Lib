package io.github.fabricators_of_create.porting_lib.gui.layered;

import io.github.fabricators_of_create.porting_lib.gui.events.RenderGuiLayerCallback;
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
 * <p>See also {@link RenderGuiLayerCallback} to intercept rendering of registered layers.
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
				.addVanilla(CAMERA_OVERLAYS, (guiGraphics, deltaTracker) -> ((GuiAccessor) getGui()).callRenderCameraOverlays(guiGraphics, deltaTracker))
				.addVanilla(CROSSHAIR, (guiGraphics, deltaTracker) -> ((GuiAccessor) getGui()).callRenderCrosshair(guiGraphics, deltaTracker))
				.addVanilla(HOTBAR)
				.addVanilla(JUMP_METER)
				.addVanilla(EXPERIENCE_BAR)
				.add(playerHealthComponents, () -> Minecraft.getInstance().gameMode.canHurtPlayer())
				.addVanilla(VEHICLE_HEALTH)
				.addVanilla(AIR_LEVEL)
				.addVanilla(SELECTED_ITEM_NAME)
				.addVanilla(SPECTATOR_TOOLTIP)
				.addVanilla(EXPERIENCE_LEVEL, (guiGraphics, deltaTracker) -> ((GuiAccessor) getGui()).callRenderExperienceLevel(guiGraphics, deltaTracker))
				.addVanilla(EFFECTS, (guiGraphics, deltaTracker) -> ((GuiAccessor) getGui()).callRenderEffects(guiGraphics, deltaTracker))
				.addVanilla(BOSS_OVERLAY, (guiGraphics, tickDelta) -> getGui().getBossOverlay().render(guiGraphics));

		GuiLayerManager additionalLayers = new GuiLayerManager()
				.addVanilla(DEMO_OVERLAY, (guiGraphics, deltaTracker) -> ((GuiAccessor) getGui()).callRenderDemoOverlay(guiGraphics, deltaTracker))
				.addVanilla(DEBUG_OVERLAY, (guiGraphics, deltaTracker) -> {
					Gui gui = getGui();
					if (gui.getDebugOverlay().showDebugScreen()) {
						gui.getDebugOverlay().render(guiGraphics);
					}
				})
				.addVanilla(SCOREBOARD_SIDEBAR, (guiGraphics, deltaTracker) -> ((GuiAccessor) getGui()).callRenderScoreboardSidebar(guiGraphics, deltaTracker))
				.addVanilla(OVERLAY_MESSAGE, (guiGraphics, deltaTracker) -> ((GuiAccessor) getGui()).callRenderOverlayMessage(guiGraphics, deltaTracker))
				.addVanilla(TITLE, (guiGraphics, deltaTracker) -> ((GuiAccessor) getGui()).callRenderTitle(guiGraphics, deltaTracker))
				.addVanilla(CHAT, (guiGraphics, deltaTracker) -> ((GuiAccessor) getGui()).callRenderChat(guiGraphics, deltaTracker))
				.addVanilla(TAB_LIST, (guiGraphics, deltaTracker) -> ((GuiAccessor) getGui()).callRenderTabList(guiGraphics, deltaTracker))
				.addVanilla(SUBTITLE_OVERLAY, (guiGraphics, deltaTracker) -> ((GuiAccessor) getGui()).getSubtitleOverlay().render(guiGraphics))
				.addVanilla(SAVING_INDICATOR, (guiGraphics, deltaTracker) -> getGui().renderSavingIndicator(guiGraphics, deltaTracker));

		layerManager
				.add(mainLayers, () -> !Minecraft.getInstance().options.hideGui)
				.addVanilla(SLEEP_OVERLAY, (guiGraphics, deltaTracker) -> ((GuiAccessor) getGui()).callRenderSleepOverlay(guiGraphics, deltaTracker))
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
