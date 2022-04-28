package io.github.fabricators_of_create.porting_lib.mixin.common;

import java.util.Set;
import java.util.function.Supplier;

import io.github.fabricators_of_create.porting_lib.util.EnchantableItem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.fabricators_of_create.porting_lib.util.EnchantmentUtil;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin {
	@Inject(method = "canEnchant", at = @At("HEAD"), cancellable = true)
	private void port_lib$canEnchant(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
		Set<Supplier<Enchantment>> enchants = EnchantmentUtil.ITEMS_TO_ENCHANTS.get(itemStack.getItem());
		if (enchants != null) {
			for (Supplier<Enchantment> enchant : enchants) {
				if (enchant.get() == (Object) this) {
					cir.setReturnValue(true);
				}
			}
		}
		if(itemStack.getItem() instanceof EnchantableItem enchantableItem) {
			cir.setReturnValue(enchantableItem.canApplyAtEnchantingTable(itemStack, (Enchantment) (Object) this));
		}
	}
}
