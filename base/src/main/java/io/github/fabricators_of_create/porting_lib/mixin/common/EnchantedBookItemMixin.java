package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import io.github.fabricators_of_create.porting_lib.enchant.CreativeModeTabPresentEnchantment;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.EnchantedBookItem;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import net.minecraft.world.item.enchantment.EnchantmentCategory;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;

@Mixin(EnchantedBookItem.class)
public abstract class EnchantedBookItemMixin {

	@Unique
	private static Enchantment port_lib$currentEnchantment = null;

	@ModifyExpressionValue(
			method = "fillItemCategory",
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/Iterator;next()Ljava/lang/Object;"
			)
	)
	private Object port_lib$storeEnchantment(Object next) {
		port_lib$currentEnchantment = (Enchantment) next;
		return next;
	}

	@ModifyExpressionValue(
			method = "fillItemCategory",
			at = @At(
					value = "FIELD",
					target = "Lnet/minecraft/world/item/enchantment/Enchantment;category:Lnet/minecraft/world/item/enchantment/EnchantmentCategory;",
					ordinal = 0
			)
	)
	private EnchantmentCategory port_lib$handleCustomEnchantmentInclusion$nullCheck(EnchantmentCategory original, CreativeModeTab tab, NonNullList<ItemStack> items) {
		if (port_lib$currentEnchantment instanceof CreativeModeTabPresentEnchantment custom) {
			boolean allowed = custom.allowedInCreativeTab((EnchantedBookItem) (Object) this, tab);
			return allowed ? EnchantmentCategory.ARMOR : null; // null check. for true, just return anything.
		}
		return original;
	}

	@ModifyExpressionValue(
			method = "fillItemCategory",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/item/CreativeModeTab;hasEnchantmentCategory(Lnet/minecraft/world/item/enchantment/EnchantmentCategory;)Z"
			)
	)
	private boolean port_lib$handleCustomEnchantmentInclusion$hasCategory(boolean original, CreativeModeTab tab, NonNullList<ItemStack> items) {
		if (port_lib$currentEnchantment instanceof CreativeModeTabPresentEnchantment custom) {
			return custom.allowedInCreativeTab((EnchantedBookItem) (Object) this, tab);
		}
		return original;
	}
}
