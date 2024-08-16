package io.github.fabricators_of_create.porting_lib.tool.mixin;

import io.github.fabricators_of_create.porting_lib.tool.ItemAbility;
import io.github.fabricators_of_create.porting_lib.tool.ItemAbilities;
import io.github.fabricators_of_create.porting_lib.tool.extensions.VanillaItemAbilityItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(ShovelItem.class)
public class ShovelItemMixin implements VanillaItemAbilityItem {
	@Override
	public boolean port_lib$canPerformAction(ItemStack stack, ItemAbility ability) {
		return ItemAbilities.DEFAULT_SHOVEL_ACTIONS.contains(ability);
	}
}
