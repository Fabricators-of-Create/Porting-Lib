package io.github.fabricators_of_create.porting_lib.mixin.common;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import io.github.fabricators_of_create.porting_lib.enchant.CustomEnchantingBehaviorItem;
import io.github.fabricators_of_create.porting_lib.enchant.CustomEnchantingTableBehaviorEnchantment;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import io.github.fabricators_of_create.porting_lib.item.CustomEnchantmentLevelItem;

import io.github.fabricators_of_create.porting_lib.item.CustomEnchantmentsItem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {
//	@Unique
//	private static Enchantment port_lib$currentEnchantment = null; TODO: PORT
//
//	@ModifyExpressionValue(
//			method = "getAvailableEnchantmentResults",
//			at = @At(
//					value = "INVOKE",
//					target = "Ljava/util/Iterator;next()Ljava/lang/Object;"
//			)
//	)
//	private static Object port_lib$grabEnchantment(Object o) {
//		if (o instanceof Enchantment e) {
//			port_lib$currentEnchantment = e;
//		}
//		return o;
//	}
//
//	/**
//	 * Same behavior as {@link EnchantmentMixin#port_lib$canEnchant(ItemStack, CallbackInfoReturnable)}
//	 */
//	@SuppressWarnings("JavadocReference")
//	@WrapOperation(
//			method = "getAvailableEnchantmentResults",
//			at = @At(
//					value = "INVOKE",
//					target = "Lnet/minecraft/world/item/enchantment/EnchantmentCategory;canEnchant(Lnet/minecraft/world/item/Item;)Z"
//			)
//	)
//	private static boolean port_lib$customEnchantability(EnchantmentCategory category, Item item, Operation<Boolean> original,
//														 int level, ItemStack stack, boolean allowTreasure) {
//		Enchantment enchantment = port_lib$currentEnchantment;
//		if (enchantment instanceof CustomEnchantingTableBehaviorEnchantment custom) {
//			// custom enchantment? let the custom logic take over
//			return custom.canApplyAtEnchantingTable(stack);
//		} else if (enchantment != null && stack.getItem() instanceof CustomEnchantingBehaviorItem custom) {
//			// enchantment not custom, but item is - let item decide
//			return custom.canApplyAtEnchantingTable(stack, enchantment);
//		}
//		// neither - vanilla logic
//		return original.call(category, item);
//	}
//
//	@ModifyReturnValue(method = "getItemEnchantmentLevel", at = @At("RETURN"))
//	private static int modifyEnchantmentLevel(int original, Enchantment enchantment, ItemStack stack) {
//		if (stack.getItem() instanceof CustomEnchantmentLevelItem custom)
//			return custom.modifyEnchantmentLevel(stack, enchantment, original);
//		return original;
//	}
//
//	@ModifyReturnValue(method = "getEnchantments", at = @At("RETURN"))
//	private static Map<Enchantment, Integer> customEnchantments(Map<Enchantment, Integer> enchantments, ItemStack stack) {
//		if (!(enchantments instanceof HashMap)) // mutability is expected, fix it if something else changed it
//			enchantments = new LinkedHashMap<>(enchantments);
//
//		if (stack.getItem() instanceof CustomEnchantmentsItem custom)
//			custom.modifyEnchantments(enchantments, stack);
//		return enchantments;
//	}
//
//	@Inject(method = "runIterationOnItem", at = @At("HEAD"), cancellable = true)
//	private static void useCustomEnchantmentList(EnchantmentHelper.EnchantmentVisitor visitor, ItemStack stack, CallbackInfo ci) {
//		if (stack.getItem() instanceof CustomEnchantmentsItem) {
//			EnchantmentHelper.getEnchantments(stack).forEach(visitor::accept);
//			ci.cancel();
//		}
//	}
}
