package io.github.fabricators_of_create.porting_lib.item;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface ArmorTickListeningItem {
	default void onArmorTick(ItemStack stack, Level level, Player player) {

	}
}
