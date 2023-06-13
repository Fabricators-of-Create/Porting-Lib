package io.github.fabricators_of_create.porting_lib.entity.client;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.world.effect.MobEffectInstance;

public interface MobEffectRenderer {
	MobEffectRenderer DEFAULT = new MobEffectRenderer() { };

	/**
	 * Queries whether the given effect should be shown in the player's inventory.
	 * <p>
	 * By default, this returns {@code true}.
	 */
	default boolean isVisibleInInventory(MobEffectInstance instance) {
		return true;
	}

	/**
	 * Queries whether the given effect should be shown in the HUD.
	 * <p>
	 * By default, this returns {@code true}.
	 */
	default boolean isVisibleInGui(MobEffectInstance instance) {
		return true;
	}

	/**
	 * Renders the icon of the specified effect in the player's inventory.
	 * This can be used to render icons from your own texture sheet.
	 *
	 * @param instance     The effect instance
	 * @param screen       The effect-rendering screen
	 * @param guiGraphics  The gui graphics
	 * @param x            The x coordinate
	 * @param y            The y coordinate
	 * @param blitOffset   The blit offset
	 * @return true to prevent default rendering, false otherwise
	 */
	default boolean renderInventoryIcon(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen, GuiGraphics guiGraphics, int x, int y, int blitOffset) {
		return false;
	}

	/**
	 * Renders the text of the specified effect in the player's inventory.
	 *
	 * @param instance     The effect instance
	 * @param screen       The effect-rendering screen
	 * @param guiGraphics  The gui graphics
	 * @param x            The x coordinate
	 * @param y            The y coordinate
	 * @param blitOffset   The blit offset
	 * @return true to prevent default rendering, false otherwise
	 */
	default boolean renderInventoryText(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen, GuiGraphics guiGraphics, int x, int y, int blitOffset) {
		return false;
	}

	/**
	 * Renders the icon of the specified effect on the player's HUD.
	 * This can be used to render icons from your own texture sheet.
	 *
	 * @param instance    The effect instance
	 * @param gui         The gui
	 * @param guiGraphics The gui graphics
	 * @param x           The x coordinate
	 * @param y           The y coordinate
	 * @param z           The z depth
	 * @param alpha       The alpha value. Blinks when the effect is about to run out
	 * @return true to prevent default rendering, false otherwise
	 */
	default boolean renderGuiIcon(MobEffectInstance instance, Gui gui, GuiGraphics guiGraphics, int x, int y, float z, float alpha) {
		return false;
	}
}
