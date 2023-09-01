package io.github.fabricators_of_create.porting_lib.item.impl.mixin;

import io.github.fabricators_of_create.porting_lib.item.api.common.addons.PiglinsNeutralItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PiglinAi.class)
public class PiglinAiMixin {
	@Inject(method = "isWearingGold", at = @At("HEAD"), cancellable = true)
	private static void port_lib$isNeutralItem(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
		for(ItemStack armor : entity.getArmorSlots())
			if(armor.getItem() instanceof PiglinsNeutralItem piglinsNeutralItem)
				cir.setReturnValue(piglinsNeutralItem.makesPiglinsNeutral(armor, entity));
	}
}
