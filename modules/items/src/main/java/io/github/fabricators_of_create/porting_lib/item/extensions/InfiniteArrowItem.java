package io.github.fabricators_of_create.porting_lib.item.extensions;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface InfiniteArrowItem {
	/**
	 * Called to determine if this arrow will be infinite when fired. If an arrow is infinite, then the arrow will never be consumed (regardless of enchantments).
	 * <p>
	 * Only called on the logical server.
	 *
	 * @param ammo The ammo stack (containing this item)
	 * @param bow  The bow stack
	 * @param livingEntity The entity who is firing the bow
	 * @return True if the arrow is infinite
	 */
	default boolean isInfinite(ItemStack ammo, ItemStack bow, LivingEntity livingEntity) {
		return false;
	}
}
