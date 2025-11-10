package io.github.fabricators_of_create.porting_lib.entity.mixin.common;

import io.github.fabricators_of_create.porting_lib.entity.events.player.UseItemOnBlockEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
	@Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
	private void callUseItemOnBlockEvent(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
		var event = new UseItemOnBlockEvent(context, UseItemOnBlockEvent.UsePhase.ITEM_AFTER_BLOCK);

		if (event.post()) {
			cir.setReturnValue(event.getCancellationResult().result());
		}
	}
}
