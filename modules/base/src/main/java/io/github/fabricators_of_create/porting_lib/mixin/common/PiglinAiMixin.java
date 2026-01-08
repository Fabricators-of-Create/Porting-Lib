package io.github.fabricators_of_create.porting_lib.mixin.common;

import com.llamalad7.mixinextras.sugar.Local;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.fabricators_of_create.porting_lib.item.PiglinsNeutralItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.item.ItemStack;

@Mixin(PiglinAi.class)
public class PiglinAiMixin {
	@Inject(method = "isWearingGold", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getItem()Lnet/minecraft/world/item/Item;"), cancellable = true)
	private static void port_lib$isNeutralItem(LivingEntity entity, CallbackInfoReturnable<Boolean> cir, @Local ItemStack armor) {
		if(armor.getItem() instanceof PiglinsNeutralItem piglinsNeutralItem) {
			cir.setReturnValue(piglinsNeutralItem.makesPiglinsNeutral(armor, entity));
		}
	}
}
