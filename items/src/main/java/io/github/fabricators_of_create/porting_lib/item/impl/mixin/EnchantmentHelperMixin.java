package io.github.fabricators_of_create.porting_lib.item.impl.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import io.github.fabricators_of_create.porting_lib.item.api.common.addons.CustomEnchantmentLevelItem;
import io.github.fabricators_of_create.porting_lib.item.api.common.addons.CustomEnchantmentsItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
	@ModifyReturnValue(method = "getItemEnchantmentLevel", at = @At("RETURN"))
	private static int modifyEnchantmentLevel(int original, Enchantment enchantment, ItemStack stack) {
		if (stack.getItem() instanceof CustomEnchantmentLevelItem custom)
			return custom.modifyEnchantmentLevel(stack, enchantment, original);
		return original;
	}

	@ModifyReturnValue(method = "getEnchantments", at = @At("RETURN"))
	private static Map<Enchantment, Integer> customEnchantments(Map<Enchantment, Integer> enchantments, ItemStack stack) {
		if (!(enchantments instanceof HashMap)) // mutability is expected, fix it if something else changed it
			enchantments = new LinkedHashMap<>(enchantments);

		if (stack.getItem() instanceof CustomEnchantmentsItem custom)
			custom.modifyEnchantments(enchantments, stack);
		return enchantments;
	}

	@Inject(method = "runIterationOnItem", at = @At("HEAD"), cancellable = true)
	private static void useCustomEnchantmentList(EnchantmentHelper.EnchantmentVisitor visitor, ItemStack stack, CallbackInfo ci) {
		if (stack.getItem() instanceof CustomEnchantmentsItem) {
			EnchantmentHelper.getEnchantments(stack).forEach(visitor::accept);
			ci.cancel();
		}
	}
}
