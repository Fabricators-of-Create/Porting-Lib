package io.github.fabricators_of_create.porting_lib.item.mixin.common;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.item.extensions.EquipmentItem;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.EquipmentDispenseItemBehavior;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EquipmentDispenseItemBehavior.class)
public class EquipmentDispenseItemBehaviorMixin {
	@Inject(method = "dispenseEquipment", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;split(I)Lnet/minecraft/world/item/ItemStack;"))
	private static void checkCanEquip(BlockSource blockSource, ItemStack item, CallbackInfoReturnable<Boolean> cir, @Local LivingEntity living, @Local EquipmentSlot slot) {
		if (item.getItem() instanceof EquipmentItem equipmentItem)
			if (!equipmentItem.canEquip(item, slot, living))
				cir.setReturnValue(false);
	}
}
