package io.github.fabricators_of_create.porting_lib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.fabricators_of_create.porting_lib.item.PiglinsNeutralItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.item.ItemStack;

@Mixin(PiglinBrain.class)
public class PiglinAiMixin {
	@Inject(method = "isWearingGold", at = @At("HEAD"), cancellable = true)
	private static void port_lib$isNeutralItem(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
		for(ItemStack armor : entity.getArmorItems())
			if(armor.getItem() instanceof PiglinsNeutralItem piglinsNeutralItem)
				cir.setReturnValue(piglinsNeutralItem.makesPiglinsNeutral(armor, entity));
	}
}
