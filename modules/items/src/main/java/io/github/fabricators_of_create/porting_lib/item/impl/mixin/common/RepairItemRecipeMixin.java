package io.github.fabricators_of_create.porting_lib.item.impl.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.item.api.extensions.RepairableItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RepairItemRecipe;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RepairItemRecipe.class)
public class RepairItemRecipeMixin {
	@ModifyExpressionValue(method = "matches(Lnet/minecraft/world/inventory/CraftingContainer;Lnet/minecraft/world/level/Level;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;canBeDepleted()Z"))
	private boolean matches$checkRepairable(boolean original, @Local(index = 6) ItemStack stack) {
		if (stack.getItem() instanceof RepairableItem repairableItem)
			return repairableItem.isRepairable(stack);
		return original;
	}

	@ModifyExpressionValue(method = "assemble(Lnet/minecraft/world/inventory/CraftingContainer;Lnet/minecraft/core/RegistryAccess;)Lnet/minecraft/world/item/ItemStack;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;canBeDepleted()Z", ordinal = 0))
	private boolean assemble$checkRepairable(boolean original, @Local(index = 6) ItemStack stack) {
		if (stack.getItem() instanceof RepairableItem repairableItem)
			return repairableItem.isRepairable(stack);
		return original;
	}

	@ModifyExpressionValue(method = "assemble(Lnet/minecraft/world/inventory/CraftingContainer;Lnet/minecraft/core/RegistryAccess;)Lnet/minecraft/world/item/ItemStack;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;canBeDepleted()Z", ordinal = 1))
	private boolean assemble$checkRepairable1(boolean original, @Local(index = 4) ItemStack stack) {
		if (stack.getItem() instanceof RepairableItem repairableItem)
			return repairableItem.isRepairable(stack);
		return original;
	}
}
