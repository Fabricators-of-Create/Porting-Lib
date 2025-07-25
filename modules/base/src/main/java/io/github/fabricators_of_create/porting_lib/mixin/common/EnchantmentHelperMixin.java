package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.enchant.CustomEnchantingBehaviorItem;
import io.github.fabricators_of_create.porting_lib.util.PortingHooks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {
	@ModifyExpressionValue(method = "runIterationOnItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/enchantment/EnchantmentHelper$EnchantmentVisitor;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getOrDefault(Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;)Ljava/lang/Object;"))
	private static Object port_lib$lookupStackEnchantments(Object original, @Local(argsOnly = true) ItemStack stack) {
		if (stack.getItem() instanceof CustomEnchantingBehaviorItem enchantingBehaviorItem) {
			var lookup = PortingHooks.resolveLookup(Registries.ENCHANTMENT);
			if (lookup != null)
				return enchantingBehaviorItem.getAllEnchantments(stack, lookup);
		}

		return original;
	}

	@ModifyExpressionValue(method = "runIterationOnItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/enchantment/EnchantmentHelper$EnchantmentInSlotVisitor;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"))
	private static Object port_lib$lookupStackEnchantments(Object original, @Local(argsOnly = true) ItemStack stack, @Local(argsOnly = true) LivingEntity entity) {
		if (stack.getItem() instanceof CustomEnchantingBehaviorItem enchantingBehaviorItem) {
			return enchantingBehaviorItem.getAllEnchantments(stack, entity.registryAccess().lookupOrThrow(Registries.ENCHANTMENT));
		}

		return original;
	}

	@ModifyExpressionValue(method = "hasTag", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getOrDefault(Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;)Ljava/lang/Object;"))
	private static Object port_lib$lookupStackEnchantments2(Object original, @Local(argsOnly = true) ItemStack stack) {
		if (stack.getItem() instanceof CustomEnchantingBehaviorItem enchantingBehaviorItem) {
			var lookup = PortingHooks.resolveLookup(Registries.ENCHANTMENT);
			if (lookup != null)
				return enchantingBehaviorItem.getAllEnchantments(stack, lookup);
		}

		return original;
	}
}
