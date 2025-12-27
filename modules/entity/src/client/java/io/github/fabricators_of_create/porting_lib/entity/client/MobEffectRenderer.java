package io.github.fabricators_of_create.porting_lib.entity.client;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Will eventually be removed in favor of the client extensions module
 */
@Deprecated
public interface MobEffectRenderer {
	Map<MobEffect, MobEffectRenderer> MOB_EFFECT_EXTENSIONS = new Reference2ObjectOpenHashMap<>();
	MobEffectRenderer DEFAULT = new MobEffectRenderer() { };

	static MobEffectRenderer of(MobEffectInstance instance) {
		return of(instance.getEffect().value());
	}

	static MobEffectRenderer of(MobEffect effect) {
		return MOB_EFFECT_EXTENSIONS.getOrDefault(effect, DEFAULT);
	}

	static void register(MobEffectRenderer extensions, MobEffect... effects) {
		if (effects.length == 0) {
			throw new IllegalArgumentException("At least one target must be provided");
		}
		Objects.requireNonNull(extensions, "Extensions must not be null");

		for (MobEffect object : effects) {
			Objects.requireNonNull(effects, "Target must not be null");
			MobEffectRenderer oldExtensions = MOB_EFFECT_EXTENSIONS.put(object, extensions);
			if (oldExtensions != null) {
				throw new IllegalStateException(String.format(
						Locale.ROOT,
						"Duplicate client extensions registration for %s (old: %s, new: %s)",
						object,
						oldExtensions,
						extensions));
			}
		}
	}

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
