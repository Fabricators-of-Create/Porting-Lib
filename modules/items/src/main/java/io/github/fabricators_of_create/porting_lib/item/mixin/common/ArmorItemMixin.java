package io.github.fabricators_of_create.porting_lib.item.mixin.common;

import com.llamalad7.mixinextras.sugar.Local;

import io.github.fabricators_of_create.porting_lib.item.extensions.EquipmentItem;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;

import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorItem.class)
public abstract class ArmorItemMixin {
	@Inject(method = "dispenseArmor", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;split(I)Lnet/minecraft/world/item/ItemStack;", ordinal = 0), cancellable = true)
	private static void port_lib$checkCanEntityEquipArmor(BlockSource blockSource, ItemStack armorItem, CallbackInfoReturnable<Boolean> cir, @Local EquipmentSlot slot, @Local LivingEntity entity) {
		if (armorItem.getItem() instanceof EquipmentItem equipmentItem && !equipmentItem.canEquip(armorItem, slot, entity)) {
			cir.setReturnValue(false);
		}
	}
}
