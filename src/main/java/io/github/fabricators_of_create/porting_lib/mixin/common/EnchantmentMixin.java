package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import io.github.fabricators_of_create.porting_lib.enchant.CustomEnchantingBehaviorItem;
import io.github.fabricators_of_create.porting_lib.enchant.CustomEnchantingTableBehaviorEnchantment;

import io.github.fabricators_of_create.porting_lib.extensions.RegistryNameProvider;

import io.github.fabricators_of_create.porting_lib.util.MixinHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin implements RegistryNameProvider {
	/**
	 * Same behavior as {@link EnchantmentHelperMixin#port_lib$customEnchantability(EnchantmentCategory, Item, Operation, int, ItemStack, boolean)}
	 */
	@SuppressWarnings("JavadocReference")
	@Inject(method = "canEnchant", at = @At("HEAD"), cancellable = true)
	private void port_lib$canEnchant(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
		if (this instanceof CustomEnchantingTableBehaviorEnchantment custom) {
			// custom enchantment? let the custom logic take over
			cir.setReturnValue(custom.canApplyAtEnchantingTable(itemStack));
		} else if (itemStack.getItem() instanceof CustomEnchantingBehaviorItem custom) {
			// enchantment not custom, but item is - let item decide
			cir.setReturnValue(custom.canApplyAtEnchantingTable(itemStack, (Enchantment) (Object) this));
		}
		// neither - vanilla logic
	}

	@Unique
	private ResourceLocation port_lib$registryName = null;

	@Unique
	@Override
	public ResourceLocation getRegistryName() {
		if (port_lib$registryName == null) {
			port_lib$registryName = Registry.ENCHANTMENT.getKey(MixinHelper.cast(this));
		}
		return port_lib$registryName;
	}
}
