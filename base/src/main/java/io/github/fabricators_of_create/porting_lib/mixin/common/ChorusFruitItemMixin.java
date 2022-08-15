package io.github.fabricators_of_create.porting_lib.mixin.common;

import io.github.fabricators_of_create.porting_lib.event.common.EntityEvents.Teleport.EntityTeleportEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ChorusFruitItem;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ChorusFruitItem.class)
public abstract class ChorusFruitItemMixin {
	@Inject(
			method = "finishUsingItem",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;randomTeleport(DDDZ)Z"),
			locals = LocalCapture.CAPTURE_FAILHARD,
			cancellable = true
	)
	private void port_lib$finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity, CallbackInfoReturnable<ItemStack> cir,
										  ItemStack superResult,
										  double d, double e, double f, // original x/y/z
										  int i,
										  double g, double h, double j) { // target x/y/z
		EntityTeleportEvent event = new EntityTeleportEvent(livingEntity, g, h, j);
		event.sendEvent();
		if (event.isCanceled()) {
			cir.setReturnValue(superResult);
		}
	}
}
