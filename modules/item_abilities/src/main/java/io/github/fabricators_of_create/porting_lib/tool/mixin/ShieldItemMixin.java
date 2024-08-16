package io.github.fabricators_of_create.porting_lib.tool.mixin;

import io.github.fabricators_of_create.porting_lib.tool.ItemAbility;
import io.github.fabricators_of_create.porting_lib.tool.ItemAbilities;
import io.github.fabricators_of_create.porting_lib.tool.extensions.VanillaItemAbilityItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(ShieldItem.class)
public class ShieldItemMixin implements VanillaItemAbilityItem {
	@Override
	public boolean port_lib$canPerformAction(ItemStack stack, ItemAbility ability) {
		return ItemAbilities.DEFAULT_SHIELD_ACTIONS.contains(ability);
	}
}
