package io.github.fabricators_of_create.porting_lib.tool.mixin;

import io.github.fabricators_of_create.porting_lib.tool.ItemAbility;
import io.github.fabricators_of_create.porting_lib.tool.ItemAbilities;
import io.github.fabricators_of_create.porting_lib.tool.extensions.VanillaItemAbilityItem;
import net.minecraft.world.item.AxeItem;

import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(AxeItem.class)
public class AxeItemMixin implements VanillaItemAbilityItem {
	@Override
	public boolean port_lib$canPerformAction(ItemStack stack, ItemAbility ability) {
		return ItemAbilities.DEFAULT_AXE_ACTIONS.contains(ability);
	}
}
