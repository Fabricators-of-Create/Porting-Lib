package io.github.fabricators_of_create.porting_lib.item.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.item.extensions.CustomEnchantmentValueItem;
import io.github.fabricators_of_create.porting_lib.item.extensions.CustomSupportsEnchantItem;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {
	@WrapOperation(method = "method_60143", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/Enchantment;isPrimaryItem(Lnet/minecraft/world/item/ItemStack;)Z"))
	private static boolean port_lib$tryCheckIsPrimaryItem(Enchantment instance, ItemStack stack, Operation<Boolean> original, @Local(argsOnly = true) Holder<Enchantment> holder) {
		if (stack.getItem() instanceof CustomSupportsEnchantItem supportsEnchantItem) {
			return supportsEnchantItem.isPrimaryItemFor(stack, holder);
		}

		return original.call(instance, stack);
	}

	@WrapOperation(method = {"getEnchantmentCost", "selectEnchantment"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;getEnchantmentValue()I"))
	private static int port_lib$tryUseStackAwareEnchantmentValue(Item instance, Operation<Integer> original, @Local(argsOnly = true) ItemStack stack) {
		if (instance instanceof CustomEnchantmentValueItem enchantmentValueItem) {
			return enchantmentValueItem.getEnchantmentValue(stack);
		}

		return original.call(instance);
	}
}
