package io.github.fabricators_of_create.porting_lib.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface InfiniteArrowItem {
	/**
	 * Allows you to override vanillas logic for checking if the player how infinite arrows
	 * See {@link net.minecraft.world.item.BowItem#releaseUsing(ItemStack, Level, LivingEntity, int)}
	 * @param projectile The current projectile to use.
	 * @param bow The bow the player is holding.
	 * @param player The player using the bow.
	 * @return Return whether the player has infinite arrows.
	 */
	boolean isInfinite(ItemStack projectile, ItemStack bow, Player player);
}
