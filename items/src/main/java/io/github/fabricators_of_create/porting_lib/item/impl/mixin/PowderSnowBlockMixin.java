package io.github.fabricators_of_create.porting_lib.item.impl.mixin;

import io.github.fabricators_of_create.porting_lib.item.api.common.addons.WalkOnSnowItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.PowderSnowBlock;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PowderSnowBlock.class)
public class PowderSnowBlockMixin {
	@Inject(method = "canEntityWalkOnPowderSnow", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
	private static void port_lib$canWalkOnSnow(Entity entity, CallbackInfoReturnable<Boolean> cir) {
		if (entity instanceof LivingEntity livingEntity && livingEntity.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof WalkOnSnowItem walkOnSnowItem)
			cir.setReturnValue(walkOnSnowItem.canWalkOnPowderedSnow(livingEntity.getItemBySlot(EquipmentSlot.FEET), livingEntity));
	}
}
