package io.github.fabricators_of_create.porting_lib.item.impl.mixin.common;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RepairItemRecipe;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RepairItemRecipe.class)
public class RepairItemRecipeMixin {
	@ModifyReturnValue(method = "canCombine", at = @At("RETURN"))
	private static boolean checkRepairable(boolean original, ItemStack itemStack, ItemStack itemStack2) {
		return original && itemStack.isRepairable() && itemStack2.isRepairable();
	}
}
