package io.github.fabricators_of_create.porting_lib.item.mixin.common;

import io.github.fabricators_of_create.porting_lib.item.extensions.PiglinsNeutralItem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.item.ItemStack;

@Mixin(PiglinAi.class)
public class PiglinAiMixin {
	@Inject(method = "isWearingGold", at = @At("HEAD"), cancellable = true)
	private static void isNeutralItem(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
		for(ItemStack armor : entity.getArmorSlots()) {
			if(armor.getItem() instanceof PiglinsNeutralItem piglinsNeutralItem) {
				cir.setReturnValue(piglinsNeutralItem.makesPiglinsNeutral(armor, entity));
				return;
			}
		}
	}
}
