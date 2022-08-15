package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.fabricators_of_create.porting_lib.enchant.CustomEnchantingTableBehaviorEnchantment;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin {
	@Inject(method = "canEnchant", at = @At("HEAD"), cancellable = true)
	private void port_lib$canEnchant(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
		if (this instanceof CustomEnchantingTableBehaviorEnchantment custom) {
			cir.setReturnValue(custom.canApplyAtEnchantingTable(itemStack));
		}
	}
}
