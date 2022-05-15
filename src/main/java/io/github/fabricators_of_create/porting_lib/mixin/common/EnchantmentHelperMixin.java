package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.enchant.CustomEnchantingTableBehaviorEnchantment;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import net.minecraft.world.item.enchantment.EnchantmentInstance;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.ListIterator;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {
	// after vanilla grabs all enchants, go back through the list and remove any
	// custom ones that were added too leniently
	@Inject(method = "getAvailableEnchantmentResults", at = @At("TAIL"))
	private static void port_lib$modifyEnchantabilityCheck(int level, ItemStack stack, boolean allowTreasure, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
		List<EnchantmentInstance> enchants = cir.getReturnValue();
		for (ListIterator<EnchantmentInstance> itr = enchants.listIterator(); itr.hasNext();) {
			EnchantmentInstance enchant = itr.next();
			if (enchant.enchantment instanceof CustomEnchantingTableBehaviorEnchantment custom) {
				if (!custom.canApplyAtEnchantingTable(stack)) {
					itr.remove();
				}
			}
		}
	}
}
