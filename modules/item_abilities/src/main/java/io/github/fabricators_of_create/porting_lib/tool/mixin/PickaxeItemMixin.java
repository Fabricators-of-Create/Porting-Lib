package io.github.fabricators_of_create.porting_lib.tool.mixin;

import io.github.fabricators_of_create.porting_lib.tool.ItemAbility;
import io.github.fabricators_of_create.porting_lib.tool.ItemAbilities;
import io.github.fabricators_of_create.porting_lib.tool.extensions.VanillaItemAbilityItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;

import org.spongepowered.asm.mixin.Mixin;

@Mixin(PickaxeItem.class)
public class PickaxeItemMixin implements VanillaItemAbilityItem {
	@Override
	public boolean port_lib$canPerformAction(ItemStack stack, ItemAbility ability) {
		return ItemAbilities.DEFAULT_PICKAXE_ACTIONS.contains(ability);
	}
}
