package io.github.fabricators_of_create.porting_lib.item.mixin.common;

import io.github.fabricators_of_create.porting_lib.item.extensions.XpRepairItem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.ItemStack;

@Mixin(ExperienceOrb.class)
public  abstract class ExperienceOrbMixin  {
	@Shadow
	private int value;

	@Unique
	private ItemStack port_lib$stackToMend = null;

	@ModifyExpressionValue(
			method = "repairPlayerItems",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/item/enchantment/EnchantedItemInUse;itemStack()Lnet/minecraft/world/item/ItemStack;"
			)
	)
	private ItemStack grabStackToMend(ItemStack original) {
		this.port_lib$stackToMend = original;
		return original;
	}

	@ModifyExpressionValue(
			method = "repairPlayerItems",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;modifyDurabilityToRepairFromXp(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;I)I"
			)
	)
	private int modifyRepairAmount(int durability) {
		ItemStack stack = port_lib$stackToMend;
		// set to null after we've used it
		port_lib$stackToMend = null;
		if (stack != null && stack.getItem() instanceof XpRepairItem custom) {
			float ratio = custom.getXpRepairRatio(stack);
			return (int) ratio * this.value;
		}
		return durability;
	}
}
