package io.github.fabricators_of_create.porting_lib.item.extensions;

import io.github.fabricators_of_create.porting_lib.item.ItemHooks;
import net.minecraft.world.item.ItemStack;

import org.jetbrains.annotations.Nullable;

// Very based clas i know
public interface CreatorModIdItem {
	/**
	 * Called to get the Mod ID of the mod that *created* the ItemStack, instead of
	 * the real Mod ID that *registered* it.
	 *
	 * For example the Forge Universal Bucket creates a subitem for each modded
	 * fluid, and it returns the modded fluid's Mod ID here.
	 *
	 * Mods that register subitems for other mods can override this. Informational
	 * mods can call it to show the mod that created the item.
	 *
	 * @param itemStack the ItemStack to check
	 * @return the Mod ID for the ItemStack, or null when there is no specially
	 *         associated mod and {@link net.minecraft.core.Registry#getKey(Object)} would return null.
	 */
	@Nullable
	default String getCreatorModId(ItemStack itemStack) {
		return ItemHooks.getDefaultCreatorModId(itemStack);
	}
}
