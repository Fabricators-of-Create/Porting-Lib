package io.github.fabricators_of_create.porting_lib.entity.mixin.teleport;

import io.github.fabricators_of_create.porting_lib.entity.events.EntityMoveEvents;
import io.github.fabricators_of_create.porting_lib.entity.events.EntityMoveEvents.EntityTeleportEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ChorusFruitItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

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
		EntityMoveEvents.TELEPORT.invoker().onTeleport(event);
		if (event.isCancelled()) {
			cir.setReturnValue(superResult);
		}
	}
}
