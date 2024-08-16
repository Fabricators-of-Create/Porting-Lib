package io.github.fabricators_of_create.porting_lib.tool.mixin;

import io.github.fabricators_of_create.porting_lib.tool.ItemAbility;
import io.github.fabricators_of_create.porting_lib.tool.ItemAbilities;
import io.github.fabricators_of_create.porting_lib.tool.extensions.VanillaItemAbilityItem;
import net.minecraft.world.item.FishingRodItem;

import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(FishingRodItem.class)
public class FishingRodItemMixin implements VanillaItemAbilityItem {
	@Override
	public boolean port_lib$canPerformAction(ItemStack stack, ItemAbility ability) {
		return ItemAbilities.DEFAULT_FISHING_ROD_ACTIONS.contains(ability);
	}
}
