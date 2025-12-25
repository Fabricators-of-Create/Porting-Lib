package io.github.fabricators_of_create.porting_lib.item.mixin.common;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.item.extensions.XpRepairItem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ExperienceOrb.class)
public  abstract class ExperienceOrbMixin  {

	@ModifyArg(
			method = "repairPlayerItems",
			index = 2,
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;modifyDurabilityToRepairFromXp(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;I)I"
			)
	)
	private int modifyRepairAmount(int durabilityToRepairFromXp, @Local ItemStack stack) {
		if (stack.getItem() instanceof XpRepairItem custom)
			return (int) (durabilityToRepairFromXp * custom.getXpRepairRatio(stack));
		return durabilityToRepairFromXp;
	}
}
