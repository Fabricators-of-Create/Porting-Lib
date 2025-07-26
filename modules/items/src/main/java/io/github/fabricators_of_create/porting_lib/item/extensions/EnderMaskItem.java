package io.github.fabricators_of_create.porting_lib.item.extensions;

import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public interface EnderMaskItem {
	/**
	 * Whether this Item can be used to hide player head for enderman.
	 *
	 * @param stack          the ItemStack
	 * @param player         The player watching the enderman
	 * @param endermanEntity The enderman that the player look
	 * @return true if this Item can be used to hide player head for enderman
	 */
	default boolean isEnderMask(ItemStack stack, Player player, EnderMan endermanEntity) {
		return stack.getItem() == Blocks.CARVED_PUMPKIN.asItem();
	}
}
