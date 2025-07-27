package io.github.fabricators_of_create.porting_lib.item.mixin.common;

import io.github.fabricators_of_create.porting_lib.item.extensions.EquipmentItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.inventory.ArmorSlot")
public abstract class ArmorSlotMixin {
	@Shadow
	@Final
	private EquipmentSlot slot;

	@Shadow
	@Final
	private LivingEntity owner;

	@Inject(method = "mayPlace", at = @At("HEAD"), cancellable = true)
	private void port_lib$checkCanEquipItem(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		if (stack.getItem() instanceof EquipmentItem equipmentItem) {
			cir.setReturnValue(equipmentItem.canEquip(stack, slot, owner));
		}
	}
}
