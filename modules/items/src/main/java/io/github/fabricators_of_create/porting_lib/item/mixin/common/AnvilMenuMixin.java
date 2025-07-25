package io.github.fabricators_of_create.porting_lib.item.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.item.extensions.CustomSupportsEnchantItem;
import net.minecraft.core.Holder;
import net.minecraft.world.inventory.AnvilMenu;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {
	@WrapOperation(method = "createResult", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;canEnchant(Lnet/minecraft/world/item/ItemStack;)Z"))
	private boolean port_lib$checkItemSupportsEnchantment(Enchantment instance, ItemStack stack, Operation<Boolean> original, @Local Holder<Enchantment> holder) {
		if (stack.getItem() instanceof CustomSupportsEnchantItem supportsEnchantItem) {
			return supportsEnchantItem.supportsEnchantment(stack, holder);
		}

		return original.call(instance, stack);
	}
}
